#include <mpi.h>
#include <pthread.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>

#include "utils.h"

/**
 * Once in a while send recent downloaded files to the tracker
 * 
 * @client:     client's info structure 
 * @phase:      INITIAL_UPDATE / ANY - first update must be followed by an ACK from the tracker
 * 
*/
void send_update_to_tracker(client_t *client, int phase) {
    int action_flag = CLIENT_UPDATE;
    int did_ACK = FALSE;
    int size_buffer;
    char *buffer;

    MPI_Send(&action_flag, 1, MPI_INT, TRACKER_RANK, 0, MPI_COMM_WORLD);
    MPI_Send(&phase, 1, MPI_INT, TRACKER_RANK, UPDATE_TRACKER_TAG, MPI_COMM_WORLD);
    MPI_Send(&client->recv_segments, 1, MPI_INT, TRACKER_RANK, UPDATE_TRACKER_TAG, MPI_COMM_WORLD);

    for (int i = 0; i < client->nof_files; ++i) {
        for (int j = 0; j < client->files[i].nof_segments; ++j) {
            if (client->files[i].segments[j].is_tracked == FALSE) {
                buffer = client->files[i].file_name;
                size_buffer = strlen(client->files[i].file_name) + 1;

                MPI_Send(&size_buffer, 1, MPI_INT, TRACKER_RANK, UPDATE_TRACKER_TAG, MPI_COMM_WORLD);
                MPI_Send(buffer, size_buffer, MPI_CHAR, TRACKER_RANK, UPDATE_TRACKER_TAG, MPI_COMM_WORLD);
                buffer = client->files[i].segments[j].segment_name;
                MPI_Send(buffer, HASH_SIZE, MPI_CHAR, TRACKER_RANK, UPDATE_TRACKER_TAG, MPI_COMM_WORLD);
                MPI_Send(&client->files[i].segments[j].idx, 1, MPI_INT, TRACKER_RANK, UPDATE_TRACKER_TAG, MPI_COMM_WORLD);

                client->files[i].segments[j].is_tracked = TRUE;
            }
        }
    }
    client->recv_segments = 0;

    if (phase == INITIAL_UPDATE) {
        MPI_Status status;
        MPI_Recv(&did_ACK, 1, MPI_INT, TRACKER_RANK, UPDATE_TRACKER_TAG, MPI_COMM_WORLD, &status);
    }
}

/**
 * Once a client sends a new segment, update tracker's info structure.
 * 
 * @tracker:          tracker's info structure
 * @client_rank:      client's rank
 * @file_name:        client's updated file
 * @segment_name:     client's new untracked segment name
 * @segment_idx:      client's new untracked segment index
 * 
*/
void add_new_segment_tracker(tracker_t *tracker, int client_rank, char *file_name, char *segment_name, int segment_idx) {
    int found_file = FALSE;
    int found_segment = FALSE;
    for (int i = 0; i < tracker->nof_files && found_file == FALSE; ++i) {
        if (!strcmp(file_name, tracker->files[i].file_name)) {
            found_file = TRUE;

            int idx = tracker->files[i].segments[segment_idx].nof_clients;
            for (int j = 0; j < tracker->files[i].segments[segment_idx].nof_clients && found_segment == FALSE; ++j) {
                if (tracker->files[i].segments[segment_idx].client_owners[j] == client_rank) {
                    found_segment = TRUE;
                }
            }
            if (found_segment == FALSE) {
                tracker->files[i].segments[segment_idx].nof_clients++;
                tracker->files[i].segments[segment_idx].client_owners[idx++] = client_rank;
            }
        }
    }
    
    if (found_file == FALSE) {
        tracker->nof_files++;
        memcpy(tracker->files[tracker->nof_files - 1].file_name, file_name, strlen(file_name));
        tracker->files[tracker->nof_files - 1].file_name[strlen(file_name)] = '\0';

        int idx = tracker->files[tracker->nof_files - 1].segments[segment_idx].nof_clients++;
        tracker->files[tracker->nof_files - 1].segments[segment_idx].client_owners[idx] = client_rank;
    }
}

/**
 * Update tracker's info about files just with new segments from every client.
 * 
 * @tracker:      tracker's info structure
 * @client_rank:  actual client sending updates
 * 
*/
void update_tracker_info(tracker_t *tracker, int client_rank, int phase) {
    int nof_files;
    MPI_Status status;
    int segment_idx, size_buffer;
    char buffer_segment_name[HASH_SIZE * 2];

    nof_files = 0;
    MPI_Recv(&nof_files, 1, MPI_INT, client_rank, UPDATE_TRACKER_TAG, MPI_COMM_WORLD, &status);

    for (int i = 0; i < nof_files; ++i) {
        memset(buffer_segment_name, 0, sizeof(buffer_segment_name));

        MPI_Recv(&size_buffer, 1, MPI_INT, client_rank, UPDATE_TRACKER_TAG, MPI_COMM_WORLD, &status);

        char buffer_file_name[size_buffer];
        memset(buffer_file_name, 0, sizeof(buffer_file_name));

        MPI_Recv(buffer_file_name, size_buffer, MPI_CHAR, client_rank, UPDATE_TRACKER_TAG, MPI_COMM_WORLD, &status);
        MPI_Recv(buffer_segment_name, HASH_SIZE, MPI_CHAR, client_rank, UPDATE_TRACKER_TAG, MPI_COMM_WORLD, &status);
        MPI_Recv(&segment_idx, 1, MPI_INT, client_rank, UPDATE_TRACKER_TAG, MPI_COMM_WORLD, &status);

        add_new_segment_tracker(tracker, client_rank, buffer_file_name, buffer_segment_name, segment_idx);
    }

    if (phase == INITIAL_UPDATE) {
        ++tracker->informed_clients;
        if (tracker->informed_clients == tracker->numtasks - 1) {
            int did_ACK = TRUE;

            for (int i = 1; i < tracker->numtasks; ++i) {
                MPI_Send(&did_ACK, 1, MPI_INT, i, UPDATE_TRACKER_TAG, MPI_COMM_WORLD);
            }
        }
    }
}

/**
 * Add recent downloaded segment in client's info.
 * 
 * @client:         client's info structure
 * @file_name:      file's name
 * @segment_name:   recent downloaded segment's name
 * @segment_idx:    recent downloaded segment's index
 * 
*/
void add_new_segment_client(client_t *client, char *file_name, char *segment_name, int segment_idx) {
    int found_file = FALSE;
    for (int i = 0; i < client->nof_files && found_file == FALSE; ++i) {
        if (!strcmp(file_name, client->files[i].file_name)) {
            found_file = TRUE;
            memcpy(client->files[i].segments[segment_idx].segment_name, segment_name, strlen(segment_name));

            client->files[i].segments[segment_idx].segment_name[HASH_SIZE] = '\0';
            client->files[i].segments[segment_idx].idx = segment_idx;
            client->files[i].segments[segment_idx].is_tracked = FALSE;
            ++client->files[i].nof_segments;
        }
    }
    if (found_file == FALSE) {
        client->nof_files++;
        client->files[client->nof_files - 1].nof_segments = 1;
        memcpy(client->files[client->nof_files - 1].file_name, file_name, strlen(file_name));
        client->files[client->nof_files - 1].file_name[strlen(file_name)] = '\0';

        memcpy(client->files[client->nof_files - 1].segments[segment_idx].segment_name, segment_name, HASH_SIZE);
        client->files[client->nof_files - 1].segments[segment_idx].segment_name[HASH_SIZE] = '\0';

        client->files[client->nof_files - 1].segments[segment_idx].idx = segment_idx;
        client->files[client->nof_files - 1].segments[segment_idx].is_tracked = FALSE;
    }
}

/**
 * Once a client finished downloading a file, write it to a file
 * 
 * @client:         client's info structure
 * @file_name:      downloaded file
 * 
*/
void write_client_file(client_t *client, char *file_name) {
    char output_file[MAX_FILENAME];
    memset(output_file, 0, sizeof(output_file));

    sprintf(output_file, "client%d_%s", client->rank, file_name);

    FILE *fd = fopen(output_file, "w");
    for (int i = 0; i < client->nof_files; ++i) {
        if (!strcmp(file_name, client->files[i].file_name)) {
            for (int j = 0; j < client->files[i].nof_segments; ++j) {
                client->files[i].segments[j].segment_name[HASH_SIZE] = '\0';
                fprintf(fd, "%s\n", client->files[i].segments[j].segment_name);
            }
        }
    }

    fclose(fd);
}

/**
 * Search a particular client for segments by asking the tracker
 * for file owners and then download from one of these who is free to communicate.
 *
 * @client:     client's info structure
 * 
*/
int search_and_download_files(client_t *client) {
    if (client->nof_wanted_files == 0) {
        int action_flag = CLIENT_ALL_DOWNLOADED;
        MPI_Send(&action_flag, 1, MPI_INT, TRACKER_RANK, 0, MPI_COMM_WORLD);
        
        return TRUE;
    }

    for (int i = 0; i < client->nof_wanted_files; ++i) {
        if (client->wanted_files[i].finished == TRUE) {
            continue;
        } else {
            int action_flag = FILE_REQUEST;
            int size_buffer = strlen(client->wanted_files[i].file_name) + 1;

            MPI_Send(&action_flag, 1, MPI_INT, TRACKER_RANK, 0, MPI_COMM_WORLD);
            MPI_Send(&size_buffer, 1, MPI_INT, TRACKER_RANK, FILE_REQUEST_TAG, MPI_COMM_WORLD);
            MPI_Send(client->wanted_files[i].file_name, size_buffer, MPI_CHAR, TRACKER_RANK, FILE_REQUEST_TAG, MPI_COMM_WORLD);
            MPI_Send(&client->wanted_files[i].required_idx_segment, 1, MPI_INT, TRACKER_RANK, FILE_REQUEST_TAG, MPI_COMM_WORLD);

            int found_client;
            MPI_Status status;

            MPI_Recv(&found_client, 1, MPI_INT, TRACKER_RANK, FILE_REQUEST_TAG, MPI_COMM_WORLD, &status);
            if (found_client == ALL_CLIENTS_OCCUPIED) {
                continue;
            } else if (found_client == OUT_OF_RANGE_SEGMENT) {
                write_client_file(client, client->wanted_files[i].file_name);
                client->wanted_files[i].finished = TRUE;

                action_flag = CLIENT_FILE_DOWNLOADED;
                MPI_Send(&action_flag, 1, MPI_INT, TRACKER_RANK, 0, MPI_COMM_WORLD);
                size_buffer = strlen(client->wanted_files[i].file_name) + 1;
                MPI_Send(&size_buffer, 1, MPI_INT, TRACKER_RANK, UPDATE_CLIENT_ROLE_TAG, MPI_COMM_WORLD);
                MPI_Send(client->wanted_files[i].file_name, size_buffer, MPI_CHAR, TRACKER_RANK, UPDATE_CLIENT_ROLE_TAG, MPI_COMM_WORLD);

                ++client->recv_files;
                if (client->recv_files == client->nof_wanted_files) {
                    action_flag = CLIENT_ALL_DOWNLOADED;
                    MPI_Send(&action_flag, 1, MPI_INT, TRACKER_RANK, 0, MPI_COMM_WORLD);
                    return TRUE;
                }
            } else {
                char segment_name[HASH_SIZE];
                memset(segment_name, 0, sizeof(segment_name));

                action_flag = FILE_REQUEST;
                MPI_Status status;

                MPI_Send(&action_flag, 1, MPI_INT, found_client, UPLOAD_TAG, MPI_COMM_WORLD);
                size_buffer = strlen(client->wanted_files[i].file_name) + 1;
                MPI_Send(&size_buffer, 1, MPI_INT, found_client, FILE_REQUEST_TAG_CLIENT, MPI_COMM_WORLD);
                MPI_Send(client->wanted_files[i].file_name, size_buffer, MPI_CHAR, found_client, FILE_REQUEST_TAG_CLIENT, MPI_COMM_WORLD);
                MPI_Send(&client->wanted_files[i].required_idx_segment, 1, MPI_INT, found_client, FILE_REQUEST_TAG_CLIENT, MPI_COMM_WORLD);
                
                MPI_Recv(segment_name, HASH_SIZE, MPI_CHAR, found_client, FILE_REQUEST_TAG_CLIENT_V_2, MPI_COMM_WORLD, &status);
                add_new_segment_client(client, client->wanted_files[i].file_name, segment_name, client->wanted_files[i].required_idx_segment);

                ++client->recv_segments;
                ++client->wanted_files[i].required_idx_segment;

                action_flag = NOTIFY_OCCUPANCY;
                MPI_Send(&action_flag, 1, MPI_INT, TRACKER_RANK, 0, MPI_COMM_WORLD);
                MPI_Send(&found_client, 1, MPI_INT, TRACKER_RANK, NOTIFY_OCCUPANCY_TAG, MPI_COMM_WORLD);
                int occupied_client = FALSE;
                MPI_Send(&occupied_client, 1, MPI_INT, TRACKER_RANK, NOTIFY_OCCUPANCY_TAG, MPI_COMM_WORLD);
                break;
            }
        }
    }
    return FALSE;
}

void *download_thread_func(void *arg)
{
    client_t *client = (client_t*) arg;
    int finished_downloading;
    send_update_to_tracker(client, INITIAL_UPDATE);
    client->recv_segments = 0;
    while (TRUE) {
        if (client->recv_segments == UPDATE_ALERT) {
            send_update_to_tracker(client, ANY);
        }
        
        for (; client->recv_segments < UPDATE_ALERT; ) {
            finished_downloading = search_and_download_files(client);
            if (finished_downloading == TRUE) {
                return NULL;
            }
        }
    }

    return NULL;
}

void *upload_thread_func(void *arg)
{
    client_t *client = (client_t*) arg;

    MPI_Status status;
    int action_flag;

    while (TRUE) {
        MPI_Recv(&action_flag, 1, MPI_INT, MPI_ANY_SOURCE, UPLOAD_TAG, MPI_COMM_WORLD, &status);
        int client_rank = status.MPI_SOURCE;

        if (action_flag == FILE_REQUEST) {
            MPI_Status status;
            int segment_idx, size_buffer;

            MPI_Recv(&size_buffer, 1, MPI_INT, client_rank, FILE_REQUEST_TAG_CLIENT, MPI_COMM_WORLD, &status);

            char buffer_file_name[size_buffer];
            memset(buffer_file_name, 0, sizeof(buffer_file_name));

            MPI_Recv(buffer_file_name, size_buffer, MPI_CHAR, client_rank, FILE_REQUEST_TAG_CLIENT, MPI_COMM_WORLD, &status);
            MPI_Recv(&segment_idx, 1, MPI_INT, client_rank, FILE_REQUEST_TAG_CLIENT, MPI_COMM_WORLD, &status);

            int found = FALSE;
            for (int i = 0; i < client->nof_files && found == FALSE; ++i) {
                if (!strcmp(buffer_file_name, client->files[i].file_name)) {
                    found = TRUE;
                    size_buffer = strlen(client->files[i].segments[segment_idx].segment_name);

                    MPI_Send(client->files[i].segments[segment_idx].segment_name, HASH_SIZE, MPI_CHAR, client_rank, FILE_REQUEST_TAG_CLIENT_V_2, MPI_COMM_WORLD);
                }
            }
        } else if (action_flag == TERMINATE) {
            return NULL;
        }
    }

    return NULL;
}

/**
 * Tracker informs the client if there is any other client available to upload his required
 * segment from a file.
 * 
 * @tracker:        tracker's info structure
 * @client_rank:    client's rank
 * 
*/
void send_file_owners(tracker_t *tracker, int client_rank) {
    MPI_Status status;
    int segment_idx;
    int found = FALSE, size_buffer;

    MPI_Recv(&size_buffer, 1, MPI_INT, client_rank, FILE_REQUEST_TAG, MPI_COMM_WORLD, &status);

    char buffer_file_name[size_buffer];
    memset(buffer_file_name, 0, sizeof(buffer_file_name));

    MPI_Recv(buffer_file_name, size_buffer, MPI_CHAR, client_rank, FILE_REQUEST_TAG, MPI_COMM_WORLD, &status);
    MPI_Recv(&segment_idx, 1, MPI_INT, client_rank, FILE_REQUEST_TAG, MPI_COMM_WORLD, &status);

    int found_client = ALL_CLIENTS_OCCUPIED;
    for (int i = 0; i < tracker->nof_files && found == FALSE; ++i) {
        if (!strcmp(buffer_file_name, tracker->files[i].file_name)) {
            if (tracker->files[i].segments[segment_idx].nof_clients == 0) {
                found_client = OUT_OF_RANGE_SEGMENT;
                break;
            }
            for (int j = 0; j < tracker->files[i].segments[segment_idx].nof_clients; ++j) {
                int client = tracker->files[i].segments[segment_idx].client_owners[j];
                if (tracker->is_occupied[client] == FALSE) {
                    tracker->is_occupied[client] = TRUE;
                    found_client = client;
                    found = TRUE;
                    break;
                }
            }
        }
    }

    MPI_Send(&found_client, 1, MPI_INT, client_rank, FILE_REQUEST_TAG, MPI_COMM_WORLD);
}

/**
 * Once a client fully downloaded a file, update his role in tracker's info
 * 
 * @tracker:        tracker's info structure
 * @client_rank:    client's rank
 * 
*/
void update_clients_role(tracker_t *tracker, int client_rank) {
    MPI_Status status;
    int size_buffer;

    MPI_Recv(&size_buffer, 1, MPI_INT, client_rank, UPDATE_CLIENT_ROLE_TAG, MPI_COMM_WORLD, &status);

    char buffer_file_name[size_buffer];
    memset(buffer_file_name, 0, sizeof(buffer_file_name));

    MPI_Recv(buffer_file_name, size_buffer, MPI_CHAR, client_rank, UPDATE_CLIENT_ROLE_TAG, MPI_COMM_WORLD, &status);

    for (int i = 0; i < tracker->nof_files; ++i) {
        if (!strcmp(buffer_file_name, tracker->files[i].file_name)) {
            tracker->files[i].roles[client_rank] = SEED;
            break;
        }
    }
}

/**
 * Update finished clients and terminate if all are done
 * 
 * @tracker:    tracker's info structure
 * 
 * Return TRUE / FALSE if all clients are done
*/
int update_clients_finished(tracker_t *tracker) {
    ++tracker->finished_clients;
    if (tracker->finished_clients == tracker->numtasks - 1) {
        int action_flag = TERMINATE;
        for (int client_rank = 1; client_rank < tracker->numtasks; ++client_rank) {
            MPI_Send(&action_flag, 1, MPI_INT, client_rank, UPLOAD_TAG, MPI_COMM_WORLD);
        }
        return TRUE;
    }
    return FALSE;
}

void update_available_clients(tracker_t *tracker, int client_rank) {
    MPI_Status status;
    int client, availability;

    MPI_Recv(&client, 1, MPI_INT, client_rank, NOTIFY_OCCUPANCY_TAG, MPI_COMM_WORLD, &status);
    MPI_Recv(&availability, 1, MPI_INT, client_rank, NOTIFY_OCCUPANCY_TAG, MPI_COMM_WORLD, &status);

    tracker->is_occupied[client] = availability;
}

void tracker(int numtasks, int rank) {
    tracker_t tracker;
    memset(&tracker, 0, sizeof(tracker));
    tracker.numtasks = numtasks;

    MPI_Status status;
    int action_flag;
    while (TRUE) {
        MPI_Recv(&action_flag, 1, MPI_INT, MPI_ANY_SOURCE, 0, MPI_COMM_WORLD, &status);
        int client_rank = status.MPI_SOURCE;
        int phase, finished_all_clients; 

        switch(action_flag) {
            case FILE_REQUEST:
                send_file_owners(&tracker, client_rank);
                break;
            case CLIENT_UPDATE:
                MPI_Recv(&phase, 1, MPI_INT, client_rank, UPDATE_TRACKER_TAG, MPI_COMM_WORLD, &status);
                update_tracker_info(&tracker, client_rank, phase);
                break;
            case CLIENT_FILE_DOWNLOADED:
                update_clients_role(&tracker, client_rank);
                break;
            case CLIENT_ALL_DOWNLOADED:
                finished_all_clients = update_clients_finished(&tracker);
                if (finished_all_clients) {
                    return;
                }
                break;
            case NOTIFY_OCCUPANCY:
                update_available_clients(&tracker, client_rank);
            default:
                break;
        }
    }
}

/**
 * Read input files for each client
 * 
 * @rank:   client's rank
 * 
 * Return client's info
*/
void read_input(client_t *client, int rank) {
    client->rank = rank;

    char file_name[MAX_FILENAME];
    memset(file_name, 0, sizeof(file_name));

    sprintf(file_name, "in%d.txt", rank);
    FILE *fd = fopen(file_name, "r");
    if (fd < 0) {
        perror("open() failed!");
        exit(-1);
    }

    fscanf(fd, "%d", &client->nof_files);
    for (int i = 0; i < client->nof_files; ++i) {
        fscanf(fd, "%s", client->files[i].file_name);
        client->files[i].file_name[strlen(client->files[i].file_name)] = '\0';
        fscanf(fd, "%d", &client->files[i].nof_segments);
        
        for (int j = 0; j < client->files[i].nof_segments; ++j) {
            fscanf(fd, "%s", client->files[i].segments[j].segment_name);

            client->files[i].segments[j].segment_name[HASH_SIZE] = '\0';
            client->files[i].segments[j].idx = j;
            client->files[i].segments[j].is_tracked = FALSE;
            client->recv_segments++;
        }
        client->files[i].role = SEED;
    }

    fscanf(fd, "%d", &client->nof_wanted_files);
    for (int i = 0; i < client->nof_wanted_files; ++i) {
        fscanf(fd, "%s", client->wanted_files[i].file_name);
        
        client->wanted_files[i].file_name[strlen(client->wanted_files[i].file_name)] = '\0';
        client->wanted_files[i].role = LEECHER;
        client->wanted_files[i].required_idx_segment = 0;
    }

    fclose(fd);
}

void peer(int numtasks, int rank) {
    pthread_t download_thread;
    pthread_t upload_thread;
    void *status;
    int r;

    client_t client;
    memset(&client, 0, sizeof(client));

    read_input(&client, rank);

    r = pthread_create(&download_thread, NULL, download_thread_func, (void *) &client);
    if (r) {
        printf("Eroare la crearea thread-ului de download\n");
        exit(-1);
    }

    r = pthread_create(&upload_thread, NULL, upload_thread_func, (void *) &client);
    if (r) {
        printf("Eroare la crearea thread-ului de upload\n");
        exit(-1);
    }

    r = pthread_join(download_thread, &status);
    if (r) {
        printf("Eroare la asteptarea thread-ului de download\n");
        exit(-1);
    }

    r = pthread_join(upload_thread, &status);
    if (r) {
        printf("Eroare la asteptarea thread-ului de upload\n");
        exit(-1);
    }
}
 
int main (int argc, char *argv[]) {
    int numtasks, rank;
 
    int provided;
    MPI_Init_thread(&argc, &argv, MPI_THREAD_MULTIPLE, &provided);
    if (provided < MPI_THREAD_MULTIPLE) {
        fprintf(stderr, "MPI nu are suport pentru multi-threading\n");
        exit(-1);
    }
    MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    if (rank == TRACKER_RANK) {
        tracker(numtasks, rank);
    } else {
        peer(numtasks, rank);
    }

    MPI_Finalize();
}
