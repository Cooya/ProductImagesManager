# Product Images Manager (freelance project)

## To build
* Bind .jar libraries to the project
* Complete the config.json file for ftp/sftp connection
```javascript
{
    host: "", // server url
    port: 21, // 21 (ftp) or 22 (sftp)
    login: "",
    password: "",
    localImagesFolder: "",
    remoteImagesFolder: "pictures", // folder where pictures will be uploaded into
    imagesUrlPrefix: "http://siteweb.com/pictures/"
}
```

### How it works
* Load the "spreadsheet.xlsx" file available at the project root via the button "Load sheet"
* Upload images from the "pictures" folder via the button "Upload images"
* Save returned URLs into the spreadsheet via the button "Save"

The password is "cooya".
