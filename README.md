# CurrencyExchangeDemo
A demo to calculate the currency exchange.

## What does it do
Currency-Exchagne-demo is a demo to calculate the currency exchange. It helps you calculate a payment from one currency to another currency.

Currency-Exchagne-demo provides two functions.
* **
  Single currency exchange calculate.
  
  User enter a payment into the console by entering a currency, an amount and followed by hitting the enter key.
  The program outputs the balance of each currency to console every minute, until the user inputs “quit”.
  
* **
  Batch currency exchange calculate.
  
  The program can read initial input payments from a file whose path is provided as a parameter when starting the program.
  The output displays an exchanged amount compared to USD, if the currency is not USD. E.g.
  
   USD 900
   CNY 2000 (USD 314.60)
   HKD 300 (USD 38.62)
  
## Quick Start

#### Step 1: Download the sorceFile package.
        Download and unzip the source file.
        
#### Step 2: Complie and install new jar package.
        cd into the project root directory.
        run the "mvn clean install" command.
        Then we can get a new currency-exchange-demo-0.0.1-SNAPSHOT.jar under {projectRootPath}/target.

#### Step 3: Run the program.
        1.if you have a batch currency data file that initial payments, you can run with following command under {projectRootPath}/target 
          "java -jar currency-exchange-demo-0.0.1-SNAPSHOT.jar --batchDataFilePath={absolute path of your batch currency data file}"
          Notice: batch currency data file only support txt.
                batch currency data file should format following example（You can refer to {projectRootPath}/src/main/resources/batchExchangeData.txt）:
                USD 900
                CNY 2000
                HKD 300
                
        2.if you process single currency exchange calculate, you can run with following command under {projectRootPath}/target 
          "java -jar currency-exchange-demo-0.0.1-SNAPSHOT.jar"
        
          After initial the exchagne rates, you can enter a payment into the console by entering a currency, an amount 
          and followed by hitting the enter key.
          The program outputs the balance of each currency to console every minute, until the user inputs “quit”.
                
        3.When starting the program, program will auto initial the exchange rates via quest the exchange rate API from https://openexchangerates.org/
          and refresh the exchange rates per 30 mins.
          if the program auto init the exchange rate fail, you must initial the exchagne rates base on USD by manusl in console.
          
 #### Step 4: Exit the program.
         enter "exit" to exit the program.
          
## Notice
       This program only test on the **Mac** platform.
       This program only supports currency exchange of USD, HKD, RMB, NZD, GBP, if you want to support more currencies,
       please modify the Currency.java under the {project root directory}/src/main/java/com/cjx/demo/model and
       add the currencies you want to support to the Enum then re do the Step 2: Complie and install new jar package in Quick start.
      
