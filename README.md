# Model Checking with the EBTSCL

This repository contains all the code files pertaining to the Master's Thesis "Model Checking with the Event-Based Time-Stamped Claim Logic" by Simão Leal.
One is encouraged to read the [thesis](Model%Checking%with%the%Event-Based%Time-Stamped%Claim%Logic.pdf) to better understand the purpose of each file.
Some concept's names have changed from their implementation to the writting of the thesis, and so some file/class/function names may be inconsistent to their "theoretical" counterparts as described in the thesis document.

## Some files which may be of special interest:
- The ClaimLang compiler: [src/claimlang/ClaimLang.jar](src/claimlang/ClaimLang.jar)
  To run it one should download the JAR file and run the following command on the command line
  ```
  java -jar <path to ClaimLang.jar> <path to file.claim>
  ```
- The ClaimLang documentation: [src/claimlang/examples/documentation.claim](src/claimlang/examples/documentation.claim)
- The VSCode extension which provides syntax highlighting for .claim files: [src/claimlang/claimlang_extension](src/claimlang/claimlang_extension)
  To install it, just download the claimlang_extension folder and place it in your VSCode extensions folder. 
