# FitTrackerApp

Track your calories intake and monitor daily activities with FitTrackerApp!

## Introduction

FitTrackerApp is a project aimed at providing users with a convenient tool to monitor their daily calorie intake and physical activities.
Leveraging external API services, this application offers seamless integration with databases containing nutritional information and exercise data.

## Instalation

1. Download repository and go to application.yml in resources file. Find datasource and fill it with your own url, username and password.

![Logo](https://i.imgur.com/agi113F.jpg)


2. Go to https://developer.edamam.com, sign in to Edamam Apis. Then go to Dashboard -> Applications and create application using Food Database API.
After that repeat creating application but this time choose Recipe Search API. When you are done you should notice two new applications.

![Logo](https://i.imgur.com/iWyboBY.jpg)

In each one click "View" button, copy Application ID and Application Keys and paste them in application.yml file.

![Logo](https://i.imgur.com/PiQw9AE.jpg)


3.  Go to https://rapidapi.com/malaaddincelik/api/fitness-calculator and choose Test Endpoint option, then after you create an account
   copy X-RapidAPI-Key and X-RapidAPI-Host

![Logo](https://i.imgur.com/g3vokF1.jpg)

Then paste them in application.yml here:                       


![Logo](https://i.imgur.com/DEpkWWC.jpg)


4. Go to https://dashboard.stripe.com/test/dashboard and create account. After that go to Developers -> API Keys -> Secret Key
    and click Reveal secret key. Then copy it.

   
![Logo](https://i.imgur.com/2d2djJ1.jpg)

Create any product and copy API ID. 

![Logo](https://i.imgur.com/UWaTgJ6.png)

Then as usual go to application.yml and paste this values. 

![Logo](https://i.imgur.com/jE7lj3N.jpg)

Go to Developers -> Webhooks and add new local listener

![Logo](https://i.imgur.com/BdMVWJW.jpg)

If you don't have domain and want to try app locally go to https://github.com/stripe/stripe-cli/releases/tag/v1.19.2 and download latest release for your OS.
Otherwise just input your endpoint.


5. (Optional) add url to frontend, you can get frontend app from my other repo https://github.com/maciejjanocinski/FitTrackerApp_frontend


   ![Logo](https://i.imgur.com/3x4C21R.jpg)

6. Clean -> Build and then ./gradlew bootRun in project directory.
   
    ![Logo](https://i.imgur.com/Mt1xcOW.png)

   
Then in CLI go to directory where you downloaded stripe CLI previously and enter this command: ./stripe.exe listen --forward-to localhost:8081/webhook
(if you want to run app on different port than 8081  you'll have to change it).


   ![Logo](https://i.imgur.com/EteJzuX.jpg)

And lastly run frontend if you have one.





