# Heider Fountain Pen Store (Open Source - Free Software)
# Android E-Commerce Shopping App  
# Heider Jeffer 
# GitHub: https://github.com/HeiderJeffer/fps19112018
# Thanks
I wanted to thank Anjan Karmakar for taking time from his busy schedule  for offering his assistance  to developed this project.
# Heider Fountain Pen Store
is  Free and open-source software application that used Android Studio to build an  E-Commerce Shopping. Heider Fountain Pen Store is Free and open-source software which mean Free like Freedom not Free Like a Free Beer, therefore we publish the source code of this app in github so anyone is free to modify, contribute, study the code of our project to make the app running in the way that he/she/or they like or to sell app,,,etc  (no license, no copy-right,,,etc).
Softwares required to developed or project: Debian GNU/Linux (highly  recommend it). Android Studio 3.4 (Version  3.4 Highly recommand it). Sublime [aptitude install sublime-text]. Bluefish editor [Gnome Store]. Libreoffice [Gnome Flatpak Store]

# Online and offline catalog/order 
The user can see the catalog of pens offline and add to cart even if there is no internet because all the details are stored locally either using the local Room database or shared preferences. However, to successfully make a payment and make a valid order the user needs the internet connection to connect to the third-party payment processor servers, in our case, Stripe.

# Integration Stripe payment
We connect to the Stripe api using secure tokenized communication and request for payment charges, done on the server side, and then receive the information back to the client side and display appropriate messages.
Shopping cart The user can add to the cart which does not involve an external database connection and so it is completely offline, the user can go to the checkout page and see the list of items added to the cart.

# Google authentication
To configure a Google API Console project, click the button below, and specify your app's package name when prompted. You will also need to provide the SHA-1 hash of your signing certificate. See Authenticating Your Client for information. 

# Linux Another Extra level of Security
We create  Superuser folder (root: ‘sudo su’ then ‘./studio.sh’ or sudo -i then ‘studio.sh’) to run the project, online compiler is off and online Gradle is off .These steps are so important to avoid or at least to minimize the Data-Collection, Spayware, Buggies Android Update and other dirty crape made by Google Devs and Google’s partners.     	 

# Google Login
Start Integrating Google Sign-In into Your Android App 
Testing  The project have a Mock test for add/delete product.

# Data
All products, images, transaction, are stored on phpmyadmin outside the android studio  

# Improve Android Studio
## Gradle Properties:
### org.gradle.daemon=true
org.gradle.parallel=true

Plugin. Uncheck:
Remove CVS Integration
Git Integration
Subversion Integration

# Setting:
Gradle: Check: Offline Work
Compiler Add: --offline
Go to File > Settings > Editor > File Types and in field Ignore files and folders add this: Thumbs.db;

# Linux:
Terminal as root copy/paste the following
sudo apt install qemu-kvm
ls -al /dev/kvm
grep kvm /etc/group
sudo adduser yourname kvm
