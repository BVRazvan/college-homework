#ifndef UTILS_H

#define UTILS_H
#define TRACKER_RANK 0
#define MAX_FILES 10
#define MAX_FILENAME 30
#define HASH_SIZE 32
#define MAX_CHUNKS 100
#define MAX_CLIENTS 10
#define UPDATE_ALERT 10
#define TRUE 1
#define FALSE 0
#define FILE_REQUEST 11
#define FILE_REQUEST_TAG 1111
#define FILE_REQUEST_TAG_CLIENT 434
#define FILE_REQUEST_TAG_CLIENT_V_2 343
#define UPLOAD_TAG 111
#define CLIENT_UPDATE 22
#define TERMINATE 222
#define UPDATE_CLIENT_ROLE_TAG 2222

#define CLIENT_FILE_DOWNLOADED 33
#define CLIENT_ALL_DOWNLOADED 44
#define ACK 55
#define NACK 66
#define INITIAL_UPDATE 77
#define UPDATE_TRACKER_TAG 777
#define ANY 88
#define ALL_CLIENTS_OCCUPIED 99
#define OUT_OF_RANGE_SEGMENT 100
#define NOTIFY_OCCUPANCY 333
#define NOTIFY_OCCUPANCY_TAG 3333

/**
 * Represents the roles a client can have regarding a file
 * 
 * @SEED:       the client has all the segments of a file
 * @PEER:       the client has a few segments of a file
 * @LEECHER:    the client has no segments of a file
 * 
*/
typedef enum role_t {
    SEED = 0,
    PEER = 1,
    LEECHER = 2
} role_t;


/**
 * Represents how a segment is seen by a client
 * 
 * @segment_name:   the name of this segment   
 * @idx:            the index of this segment in its file
 * @is_tracked:     whether the segment is tracked or not by the tracker
 * 
*/
typedef struct segment_t {
    char segment_name[HASH_SIZE * 2];
    int idx;
    int is_tracked;
} segment_t;


/**
 * Represents how a file is seen by a client
 * 
 * @file_name:              the name of the file
 * @segments:               segments' structure
 * @nof_segments:           number of segments in this file
 * @role:                   the role of the client regarding this file
 * @finished:               whether the file is fully downloaded or not
 * @required_idx_segment:   the index of the next segment of partially downloaded file
 * 
*/
typedef struct client_file_t {
    char file_name[MAX_FILENAME];
    segment_t segments[MAX_CHUNKS];
    int nof_segments;
    role_t role;

    int finished;
    int required_idx_segment;
} client_file_t;

/**
 * Represents the structure of a client
 * 
 * @rank:               the rank of the client                 
 * @files:              the array of partially/fully owned files
 * @wanted_files:       the array of wanted files
 * @nof_wanted_files:   the number of wanted files
 * @recv_files:         the number of downloaded files
 * @recv_segments:      the number of received segments, helps for updating the tracker when the number is a multiple of UPDATE_ALERT
 * 
*/
typedef struct client_t {
    int rank;
    client_file_t files[MAX_FILES];
    int nof_files;
    client_file_t wanted_files[MAX_FILES];
    int nof_wanted_files;

    int recv_files;
    int recv_segments;
} client_t;

/**
 * Represents how a segment is seen by the tracker
 * 
 * @segment_name:   the name of the segment
 * @idx:            the index of segment inside the file
 * @client_owners:  the array of clients owning this segment
 * @nof_clients:    the number of clients owning this segment
 * 
*/
typedef struct tracker_segment_t {
    char segment_name[HASH_SIZE * 2];
    int idx;
    int client_owners[MAX_CLIENTS];
    int nof_clients;
} tracker_segment_t;


/**
 * Represents how a file is seen by the tracker
 * 
 * @file_name:  the name of the file
 * @segments:   the array of segments of this file
 * @roles:      the roles of clients regarding this file
 * 
*/
typedef struct tracker_file_t {
    char file_name[MAX_FILENAME];
    tracker_segment_t segments[MAX_CHUNKS];
    role_t roles[MAX_CLIENTS];
} tracker_file_t;

/***
 * Represents the structure of the tracker
 * 
 * @files:              the array of updated files
 * @nof_files:          the number of files updated
 * @finished_clients:   the number of clients who have finished downloading desired files
 * @informed_clients:   the number of informed clients in order to start downloading desired files
 * @numtasks:           the number of clients
 * @is_occupied:        the array which tells whether a client is occupied uploading a file for another client
 * 
*/
typedef struct tracker_t {
    tracker_file_t files[MAX_FILES];
    int nof_files;
    int finished_clients;
    int informed_clients;
    int numtasks;
    int is_occupied[MAX_CLIENTS];
} tracker_t;

#endif