This is a simulation of a student's portal for course registration.

This project is to demonstrate RESTful services, hence the focus is more on REST methods rather than other aspects like UI design, DB backend services or large data.

Details of the data used:
There are 2 students with registration numbers: 17bce1236 and 17bec1131.
There are 2 subjects for which the students can register: Machine learning (code ml) and Software Development(code sd).
There are 2 teachers for Machine Learning: mlt1 and mlt2 teaching in slots l1 and l2 respectively.
There are 3 teachers for Software Development: sdt1, sdt2 and sdt2 teaching in slots l2, l3 and l4 respectively.
There are 5 seats available for each slot of a particular teacher for that subject.

Steps to execute:
1) Download all the files and place them in the same folder.
2) test1.js the driver file. Run the test1.js file using node, nodemon or other suitable means and then type http://localhost:1234 in your address bar.
The app will navigate you thereafter.

File Details:
The courses currently registered by the particular student is saved by writing to the students.json file. The subject details(teachers teaching, number of seats currently available etc) are saved in the sd.json and ml.json file for the respective subjects.

Template:
pug template has been used for this project

Shortcomings:
Once you add remove a course, the change is visible if you view the students.json file. But to view this change via the 'View registered Courses' button in the menu.pug file, type http://localhost:1234 in your address bar again, enter the reg no again and then click on the 'View registered Courses' button to view the changes in the browser.




