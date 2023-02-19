// Bogdan Valentin-Razvan 321CA OOP 2022-2023 <bogdanvrazvan@gmail.com>

	POO TV
        -------
	
	This project has the purpose to create a platform like Netflix, HBO-GO,
etc using design patterns to solve particular tasks.

------------------------------------------------------------------------------------------
            |
First Stage |
____________|

	Firstly, I implemented the mechanism for changing pages (page navigator).
For this task, I used "State" design pattern in this manner: I defined
a "page state" interface which contains all the possible methods for changing to
a different page, followed by creating a class for every page; these implement
previous methods, accordingly to page's description. In the end, a general class
containing instances of pages' classes keeps track on which page the user is.

             / change to page_1 |                 |- page_1 |
	    /                   |                 |         |
page state / .................. | implemented by=>|- ...... | contained in=> page navigator
	   \                    |                 |         |                      ||
	    \                   |                 |         |                user actual page
             \ change to page_n |                 |- page_n |

------------------------------------------------------------------------------------------

	Secondly, I created the flow for doing actions inside a page. Here, I used
"Visitor" design pattern. Therefore, the page navigator class accepts a visitor which is
then redirected towards the particular page on which the user is and finally the operation
is applied, accordingly to page's description.
	Moreover, I used lazy "Singleton" design pattern for visitors, as they only have to
be instantiated once.


          / action on page_1 |                 |- action_1 |
	 /                   |                 |           |
visitor / .................. | implemented by=>|- ........ | accepted by => page navigator
	\                    |                 |           |                   ||
	 \                   |                 |           |               redirected to
          \ action on page_n |                 |- action n |                 / |\
                                                                            /  | \
                                                                           /   |  \
                                                                          /    |   \
                                                                     page_1  .... page_n

-----------------------------------------------------------------------------------------

	The execution goes in the following manner:
		-Database is created from input data;
		-Page navigator is placed in the fundamental page;
		-Actions are being executed, having a type as:
			-register/login/logout which requests data from database;
			-page changes which involve using page navigator;
			-on page actions which involve using visitors.
		-Execution ends.

-----------------------------------------------------------------------------------------

             |
Second Stage |
____________ |

	This stage comes as a continuation to the first one, adding more features to the
platform.

-----------------------------------------------------------------------------------------

	"Back" button

	- enables the posibility to navigate back on the last accesed page. For this
purpose, I used an arraydeque which saves "change page" actions:
		- Once a "change page" action happens, add it to the front of the deque;
		- Once a "back" action happens, remove first action from arraydeque,
	representing actual page, and then remove once again first action in arraydeque,
	representing last page on which user will nagivate now by applying "change page"
	action to this page.

-----------------------------------------------------------------------------------------
	
	Database changes & subscription to genres

	- I will talk about these two features together because I used observer design
pattern to implement them as a whole.
	- database changes enable the posiblity to modifity the database of movies and
subscription feature allows users to get information about a particular type of movies.
	- as previously said, observer design pattern was used to implement these
features in this way:
		- movies and genres are seen as subjects in this design pattern and
	users are observers, being notified and updated when changes to subjects occurs.
	Thus, I used two hashmaps: (Genre, Arraylist Users), (Movie, Arraylist Users).
	Subscribe method:
			- to genre: by applying "subcribe" action from a user;
			- to movie: by applying "purchase" action from a user.
	Unsubscribe method:
			- to movie: by applying "database delete action".
	Notify and update:
			- by genre: when applying "database add" action, users having a
		subscription to one of movies' genres.
			- by movie: when applying "database remove" action, users having
		at least purchased subject movie.

-----------------------------------------------------------------------------------------

	Movie recommendation

	- once users' interaction with the platform ends, actual connected premium user
gets a movie recommendation based on multiple facts, in this order:
		1. Generate a hashmap (genre, likes) based on actual user's list of liked
	movies' genres and sort this hashmap decreasingly by likes and increasingly by
	name.
		2. Sort accesibly movies by the user by the number of likes.
		3. Find the first unwatched movie from this list having a genre with height
	possible place in previously generated hashmap and send it as recommendation.

-----------------------------------------------------------------------------------------









