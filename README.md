qualtrics2sheets
================
A utility for exporting a survey from Qualtrics and importing the resulting CSV into Google Sheets
(all in one command)

Rationale
---------
If you want to work with your Qualtrics data in Google Sheets, you typically have to achieve this manually by:

1. Exporting the data from Qualtrics
2. Downloading the exported file
3. Unzipping the CSV
4. Uploading the CSV to Google Drive
5. Converting the CSV to a Google Sheets spreadsheet

This utility exists to automate these steps.
Specifically, you can:

1. Download the Qualtrics CSV
2. Upload it (or any other CSV!) to Sheets
3. Do it all in one go


Installation
------------

### Prerequisites
To run this program, you need Java 15 or later on your system.
(It should be possible to recompile the program for older versions of Java as well.)

### Obtain the program
Download it from releases or compile it yourself by running `./gradlew shadowJar`.
(The compiled program will be at `qualtrics2sheets/build/libs/qualtrics2sheets-all.jar`.)


Setup
-----
### Qualtrics
You'll need your Qualtrics API token, which you can obtain by following
[this Qualtrics documentation](https://api.qualtrics.com/guides/docs/Instructions/api-key-authentication.md).

Once you have it, you'll pass it to `qualtrics2sheets` using the `--token` flag.

### Google Sheets
You'll need a Google Cloud credentials file, which you can obtain by following
[Google's documentation here](https://developers.google.com/workspace/guides/create-credentials#desktop).
Note that you'll want to create a new project in the Cloud Console before generating the new credentials.
Once you have the credentials file, save it somewhere on disk; you'll pass it to `qualtrics2sheets` using the `--credentials` flag.

The first time you run `qualtrics2sheets`, it will need to use those credentials to obtain an authentication token (which is what's actually used to interact with the API).
To do this, it will open a new browser window, where you'll be asked to authorize the application you created in the Cloud Console with access to Sheets.


Usage
-----
**Warning**:
when you run the program, it will overwrite the contents of the target spreadsheet.
Make sure you're not losing any data.

Here is a sample of how to run the program:

    qualtrics2sheets run --datacenter ca1 --token $QUALTRICS_TOKEN --credentials /path/to/credentials.json --spreadsheet 1xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx --survey SV_xxxxxxxxxxxxxxx

- `--datacenter` is your account's Qualtrics datacenter, [see the documentation here](https://api.qualtrics.com/guides/docs/Instructions/base-url-and-datacenter-ids.md)
- `--spreadsheet` is the ID of your spreadsheet (you can get it from its URL)
- `--survey` is the ID of your Qualtrics survey (you can also get it from its URL)


You can also run the pipeline steps separately:

    qualtrics2sheets download_qualtrics
    qualtrics2sheets upload_csv
