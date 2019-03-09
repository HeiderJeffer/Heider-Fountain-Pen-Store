# Heider Fountain Pen Store

Android Project - Heider Fountain Pens store

Features:

- online and offline catalog/order [DONE]
The user can see the catalog of pens offline and add to cart even if there is no internet because all the details are stored locally either using the local Room database or shared preferences. However, to successfully make a payment and make a valid order the user needs the internet connection to connect to the third-party payment processor servers, in our case, Stripe.


- integration Stripe payment [DONE]
We connect to the Stripe api using secure tokenized communication and request for payment charges, done on the server side, and then receive the information back to the client side and display appropriate messages.

- shopping cart [DONE]
The user can add to the cart which does not involve an external database connection and so it is completely offline, the user can go to the checkout page and see the list of items added to the cart. 

- Google authentication [DONE]

- Google Login [Done]

- The application should be robust [DONE]
Appropriate error handling is done in all parts of the application and the user is prompted with error messages when necessary. Also, the security in communicating with the stripe api is taken into consideration which grants it more robustness as the communication is securely tokenized.  

- Speed Up F##king Android Studio

1. Gradle Properties:
org.gradle.daemon=true
org.gradle.parallel=true

2. Plugin. Uncheck:
1. Remove CVS Integration
2. Git Integration
3. Subversion Integration

3. Gradle:
Check: Offline Work

4. Compiler
Add:  --offline

5. Go to File > Settings > Editor > File Types 
and in field Ignore files and folders add this: Thumbs.db;


Android For Linux:


sudo apt install qemu-kvm.

ls -al /dev/kvm

grep kvm /etc/group

sudo adduser yourname kvm
