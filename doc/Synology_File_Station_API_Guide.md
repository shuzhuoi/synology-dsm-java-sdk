# Synology File Station

Official API 

THIS DOCUMENT CONTAINS PROPRIETARY TECHNICAL INFORMATION WHICH IS THE PROPERTY OF SYNOLOGY INCORPORATED AND SHALL NOT BE REPRODUCED, COPIED, OR USED AS THE BASIS FOR DESIGN, MANUFACTURING, OR SALE OF APPARATUS WITHOUT WRITTEN PERMISSION OF SYNOLOGY INCORPORATED 

## Table of Contents

Chapter 1: Introduction 1.1
Chpater 2: Getting Started 1.2
API Workflow 1.2.1
Making Requests 1.2.2
Parsing Response 1.2.3
Common Error Codes 1.2.4
Working Example 1.2.5
Chpater 3: Base API 1.3
SYNO.API.Info 1.3.1
SYNO.API.Auth 1.3.2
Chpater 4: File Station API 1.4
SYNO.FileStation.Info 1.4.1
SYNO.FileStation.List 1.4.2
SYNO.FileStation.Search 1.4.3
SYNO.FileStation.VirtualFolder 1.4.4
SYNO.FileStation.Favorite 1.4.5
SYNO.FileStation.Thumb 1.4.6
SYNO.FileStation.DirSize 1.4.7
SYNO.FileStation.MD5 1.4.8
SYNO.FileStation.CheckPermission 1.4.9
SYNO.FileStation.Upload 1.4.10
SYNO.FileStation.Download 1.4.11
SYNO.FileStation.Sharing 1.4.12
SYNO.FileStation.CreateFolder 1.4.13
SYNO.FileStation.Rename 1.4.14
SYNO.FileStation.CopyMove 1.4.15
SYNO.FileStation.Delete 1.4.16
SYNO.FileStation.Extract 1.4.17
SYNO.FileStation.Compress 1.4.18
SYNO.FileStation.BackgroundTask 1.4.19
Appendix A: Release Notes 1.5 

## Chapter 1: Introduction

This File Station Official API developer's guide explains how to expand your applications based on the APIs of File Station, allowing your applications to interact with files in DSM via HTTP/HTTPS requests and responses. 

This document explains the structure and detailed specifications of various File Station APIs. "Chapter 2: Get Started" describes the basic guidelines on how to use these APIs, which we suggest reading all the way through before you jump into the API specifications. "Chapter 3: Base API" and "Chapter 4: File Station API" list all available APIs and related details. 

THIS DOCUMENT CONTAINS PROPRIETARY TECHNICAL INFORMATION WHICH IS THE PROPERTY OF SYNOLOGY INCORPORATED AND SHALL NOT BE REPRODUCED, COPIED, OR USED AS THE BASIS FOR DESIGN, MANUFACTURING, OR SALE OF APPARATUS WITHOUT WRITTEN PERMISSION OF SYNOLOGY INCORPORATED 

## Copyright

Synology Inc. ® 2023 Synology Inc. All rights reserved. 

No part of this publication may be reproduced, stored in a retrieval system, or transmitted, in any form or by any means, mechanical, electronic, photocopying, recording, or otherwise, without prior written permission of Synology Inc., with the following exceptions: Any person is hereby authorized to store documentation on a single computer for personal use only and to print copies of documentation for personal use provided that the documentation contains Synology's copyright notice. 

The Synology logo is a trademark of Synology Inc. 

No licenses, express or implied, are granted with respect to any of the technology described in this document. Synology retains all intellectual property rights associated with the technology described in this document. This document is intended to assist application developers to develop applications only for Synology-labeled computers. 

Every effort has been made to ensure that the information in this document is accurate. Synology is not responsible for typographical errors. 

Synology Inc. 9F., No.1, Yuandong Rd., New Taipei City 220632, Taiwan 

Synology and the Synology logo are trademarks of Synology Inc., registered in the United States and other countries. 

Marvell is registered trademarks of Marvell Semiconductor, Inc. or its subsidiaries in the United States and other countries. 

Freescale is registered trademarks of Freescale. Intel and Atom is registered trademarks of Intel. 

Semiconductor, Inc. or its subsidiaries in the United States and other countries. 

Other products and company names mentioned herein are trademarks of their respective holders. 

Even though Synology has reviewed this document, SYNOLOGY MAKES NO WARRANTY OR REPRESENTATION, EITHER EXPRESS OR IMPLIED, WITH RESPECT TO THIS DOCUMENT, ITS QUALITY, ACCURACY, MERCHANTABILITY, OR FITNESS FOR A PARTICULAR PURPOSE. AS A RESULT, THIS DOCUMENT IS PROVIDED "AS IS," AND YOU, THE READER, ARE ASSUMING THE ENTIRE RISK AS TO ITS QUALITY AND ACCURACY. IN NO EVENT WILL SYNOLOGY BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES RESULTING FROM ANY DEFECT OR INACCURACY IN THIS DOCUMENT, even if advised of the possibility of such damages. 

THE WARRANTY AND REMEDIES SET FORTH ABOVE ARE EXCLUSIVE AND IN LIEU OF ALL OTHERS, ORAL OR WRITTEN, EXPRESS OR IMPLIED. No Synology dealer, agent, or employee is authorized to make any modification, extension, or addition to this warranty. 

Some states do not allow the exclusion or limitation of implied warranties or liability for incidental or consequential damages, so the above limitation or exclusion may not apply to you. This warranty gives you specific legal rights, and you may also have other rights which vary from state to state. 

## Chpater 2: Getting Started

Before making use of File Station APIs to develop your own applications, you need to have basic understanding of API concepts and API procedures. 

This chapter explains how to execute and complete API processes in the following five sections: 

- API Workflow: Briefly introduces how to work with File Station APIs 

- Making Requests: Elaborates on how to construct API requests 

- Parsing Response: Describes how to parse response data 

- Common Error Codes: Lists all common error codes that might be returned from all File Station APIs 

- Working Example: Provides an example to request a file operation 

## API Workflow

The following five-step and easy-to-follow workflow shows how to make your application interact with File Station APIs. 

![image](https://cdn-mineru.openxlab.org.cn/result/2026-06-30/55dad977-6ffa-42d1-a987-469c75a94ffc/bc8c8c1572cea6f9a10b38795372dfcb8458eb7ac6adfd69d1eca3c512e6c15a.jpg)



Step 1: Retrieve API Information


First, your application needs to retrieve API information from the target DiskStation to know which APIs are available for use on the target DiskStation. This information can be accessed simply through a request to /webapi/query.cgi with SYNO.API.Info API parameters. The information provided in the response contains available API name, API method, API path and API version. Once you have all the information on hand, your application can make further requests to all available APIs. 

Step 2: Log in 

In order to make your application interact with File Station, your application needs to log in with an account and password first. The login process is simply making a request to SYNO.API.Auth API with the login method. If successful, the API returns an authorized session ID. You should keep it and pass it on making other API requests. 

## Step 3: Making API Requests

Once successfully logged in, your application can start to make requests to all available File Station APIs. In the next section, "Making Requests", instructions on how to form a valid API request and how to decode response information will be given. 

## Step 4: Log out

After finishing the steps above, your application can end the login session by making another request to SYNO.API.Auth API with the logout method. 

## Making Requests

There are five basic elements that are used to construct a valid request to any API. 

• API name: Name of the API requested 

- version: Version of the API requested 

- path: path of the API. The path information can be retrieved by requesting SYNO.API.Info 

• method: Method of the API requested 

- _sid: Authorized session ID. Each API request should pass it, which is retrieved from the response of /webapi/auth.cgi, via either HTTP/HTTPS GET/POST method with _sid argument. Otherwise, if you pass it within id value of cookie of HTTP/HTTPS header, this parameter can be ignored. 

And the syntax for the request is as follows: 

GET /webapi/<CGI_PATH>?api=<API_NAME>&version=<VERSION>&method=<METHOD>[&<PARAMS>][&_sid=<SID>] 

Here <PARAMS> represents the parameters for the requested method which is optional. Note all parameters need to be escaped. Commas "," are replaced by slashes "\"", and slashes "\"" are replaced by double-slashes "\"", because commas "," are used to separate multiple elements in a parameter. Password-relative parameters do not need to be escaped including passwd or password parameter. 

Please see the following example. If you want to make a request to the SYNO.API.Info API version 1 with the query method on your DiskStation whose address is http://myds.com:port (default ports for HTTP and HTTPS are 5000 or 5001, respectively) for the list of all available API methods, the corresponding parameters are: 

• API name: SYNO.API.Info 

- version: 1 

- path: query.cgi 

- method: query 

- params: query=all 

And the request will look like this: 

## Parsing Response

All API responses are encoded in the JSON format, and the JSON response contains elements as follows: 

<table><tr><td>Key</td><td>Value</td><td>Description</td></tr><tr><td>success</td><td>true/false</td><td>&quot;true&quot;: the request finishes successfully, &quot;false&quot;: the request fails with an error data.</td></tr><tr><td>data</td><td></td><td>The data object contains all response information described in each method.</td></tr><tr><td>error</td><td></td><td>The data object contains error information when a request fails. The basic elements are described in the next table.</td></tr></table>

The following describes the format of error information in error element. 

<table><tr><td>Key</td><td>Value</td><td>Description</td></tr><tr><td>code</td><td>Error Code</td><td>An error code will be returned when a request fails. There are two kinds of error codes: a common error code which is shared between all APIs; the other is a specific API error code (described under the corresponding API spec).</td></tr><tr><td>errors</td><td></td><td>The array contains detailed error information of each file. Each element within errors is a JSON-Style Object which contains an error code and other information, such as a file path or name.Note: When there is no detailed information, this error element will not respond.</td></tr></table>

## Example 1

Respond an invalid request to get information of File Station without a method parameter. 

Request: 

http://myds.com:port/webapi/entryFilStation/info.cgi?api=SYNO.FileStation.Info&version=2&method=get 

Failed Response: 

```json
{
    "success": false,
    "error": {
    "code": 101
    }
} 
```

## Example 2

Respond an invalid request with an illegal path to create a folder. 

Request: 

http://myds.com:port/webapi/FilStation/info.cgi?api=SYNO.FileStation.CreateFolder&method=create&version=1&folder_path=%2Ftest&name=%3A 

## Failed Response:

```json
{
    "success": false,
    "error": {
    "code": 1100,
    "errors": [
    {
    "code": 418,
    "path": "/test/:"
    }
    ]
    }
} 
```

## Example 3

Respond a successful request to get information from File Station. 

Request: 

http://myds.com:port/webapi/FilStation/info.cgi?api=SYNO.FileStation.Info&version=2&method=get 

Success Response: 

```json
{
    "success": true,
    "data": {
    "is_manager": true,
    "hostname": "DS",
    "support_sharing": true,
    "support_virtual": "cifs,iso"
    }
} 
```

Note that to demonstrate examples with clarity, only the data object is included in the response examples given in the following sections. 

## Common Error Codes

The codes listed below are common error codes of wrong parameters or failed login for all WebAPIs. 

<table><tr><td>Code</td><td>Description</td></tr><tr><td>100</td><td>Unknown error</td></tr><tr><td>101</td><td>No parameter of API, method or version</td></tr><tr><td>102</td><td>The requested API does not exist</td></tr><tr><td>103</td><td>The requested method does not exist</td></tr><tr><td>104</td><td>The requested version does not support the functionality</td></tr><tr><td>105</td><td>The logged in session does not have permission</td></tr><tr><td>106</td><td>Session timeout</td></tr><tr><td>107</td><td>Session interrupted by duplicate login</td></tr><tr><td>119</td><td>SID not found</td></tr></table>


The codes listed below are common error codes of file operations for all File Station APIs. 


<table><tr><td>Code</td><td>Description</td></tr><tr><td>400</td><td>Invalid parameter of file operation</td></tr><tr><td>401</td><td>Unknown error of file operation</td></tr><tr><td>402</td><td>System is too busy</td></tr><tr><td>403</td><td>Invalid user does this file operation</td></tr><tr><td>404</td><td>Invalid group does this file operation</td></tr><tr><td>405</td><td>Invalid user and group does this file operation</td></tr><tr><td>406</td><td>Can&#x27;t get user/group information from the account server</td></tr><tr><td>407</td><td>Operation not permitted</td></tr><tr><td>408</td><td>No such file or directory</td></tr><tr><td>409</td><td>Non-supported file system</td></tr><tr><td>410</td><td>Failed to connect internet-based file system (e.g., CIFS)</td></tr><tr><td>411</td><td>Read-only file system</td></tr><tr><td>412</td><td>Filename too long in the non-encrypted file system</td></tr><tr><td>413</td><td>Filename too long in the encrypted file system</td></tr><tr><td>414</td><td>File already exists</td></tr><tr><td>415</td><td>Disk quota exceeded</td></tr><tr><td>416</td><td>No space left on device</td></tr><tr><td>417</td><td>Input/output error</td></tr><tr><td>418</td><td>Illegal name or path</td></tr><tr><td>419</td><td>Illegal file name</td></tr><tr><td>420</td><td>Illegal file name on FAT file system</td></tr><tr><td>421</td><td>Device or resource busy</td></tr><tr><td>599</td><td>No such task of the file operation</td></tr></table>

## Working Example

The following demonstrates a working example for requesting a file operation from the DiskStation. To implement this example, simply replace the DiskStation address used in the example (myds.com:port) with your DiskStation address and paste the URL to a browser. Then the JSON response will show up in a response page. 

## Step 1: Retrieve API Information

In order to make API requests, you should first request to /webapi/query.cgi with SYNO.API.Info to get the SYNO.API.Auth API information for logging in and FileStation API info for file operations. 

## Request:

```javascript
http://myds.com:port/webapi/query.cgi?api=SYNO.API.Info&version=1&method=query&query=SYNO.API.Auth,SYNO.FileStation 
```

## Response:

```json
{
    "data": {
    "SYNO.API.Auth": {
    "path": "auth.cgi",
    "minVersion": 1,
    "maxVersion": 3
    },
    "SYNO.FileStation.List": {
    "path": "FileStation/file_share.cgi",
    "minVersion": 1,
    "maxVersion": 1
    }
},
"success": true
} 
```

## Step 2: Login

After the SYNO.API.Auth path and supported version information are returned, you can log in a FileStation session by requesting SYNO.API.Auth API version 3 located at /webapi/auth.cgi. 

## Request:

```txt
http://myds.com:port/webapi/auth.cgi?api=SYNO.API.Auth&version=3&method=login&account=admin&passwd=12345&session=FileStation&format=cookie 
```

## Response:

```json
{
    "data": {
    "sid": "ohOCjwhHhwghw"
    },
    "success": true
} 
```

## Step 3: Request a File Station API

After a session is logged in, you can continue to call the method of listing shared folder in SYNO.FileStation.List. The cgi path and version are provided in the response of Step 1, and the list of all tasks can be requested by excluding the offset and limit parameters. 

## Request:

http://myds.com:port/webapi/FileStation/file_share.cgi?api=SYNO.FileStation.List&version=1&method=list_share 

## Response:

```json
{
    "data": {
    "offset": 0,
    "shares": [
    {
    "isdir": true,
    "name": "video",
    "path": "/video"
    },
    {
    "isdir": true,
    "name": "photo",
    "path": "/photo"
    }
    ],
    "total": 2
},
"success": true
} 
```

From the response list, it can be observed that there are two shared folders in File Station. Let's say you're interested in the shared folder "photo" and want to know more details about it, you can make another request to the getinfo method. In this request, you will need to add the parameter additional=real_path, owner, time for the method to request detailed objects and transfer them in response. 

## Request:

## Response:

```javascript
http://myds.com:5000/webapi/FileStation/file_share.cgi?api=SYNO.FileStation.List&version=1&method=getinfo&path=%2Fphoto&additional=real_path,owner,time,perm 
```

```json
{
    "data": {
    "files": [
    {
    "additional": {
    "owner": {
    "gid": 100,
    "group": "users",
    "uid": 1024,
    "user": "admin"
    },
    "real_path": "/volume1/photo",
    "time": {
    "atime": 1371630215,
    "crtime": 1352168821,
    "ctime": 1368769689,
    "mtime": 1368769689
    }
    },
    "isdir": true,
    "name": "photo",
    "path": "/photo"
    }
    ]
},
"success": true
} 
```

## Step 4: Logout

When finished with the procedure, you should log out of the current session. The session will be ended by calling the logout method in SYNO.API.Auth. If you want to log out a specific session, you can pass the _sid parameter. 

## Example:



http://myds.com:5000/webapi/auth.cgi?api=SYNO.API.Auth&version=1&method=logout&session=FileStation 



## Chpater 3: Base API

API List 

The following table is the overview of two fundamental APIs defined in this chapter: 

<table><tr><td>API Name</td><td>Description</td></tr><tr><td>SYNO.API.Info</td><td>Provide available API info.</td></tr><tr><td>SYNO.API.Auth</td><td>Perform login and logout.</td></tr></table>

# SYNO.API.Info

Overview 

Availability: Since DSM 4.0 

Version: 1 

## Method

Query 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Availability</td></tr><tr><td>query</td><td>API names, separated by a comma &quot;,&quot; or use &quot;all&quot; to get all supported APIs.</td><td>1 and later</td></tr></table>

## Example:

GET /webapi/query.cgi?api=SYNO.API.Info&version=1&method=query&query=all 

## Response:

Contains API description objects. 

<table><tr><td>Parameter</td><td>Description</td><td>Availability</td></tr><tr><td>key</td><td>API name.</td><td>1 and later</td></tr><tr><td>path</td><td>API path.</td><td>1 and later</td></tr><tr><td>minVersion</td><td>Minimum supported API version.</td><td>1 and later</td></tr><tr><td>maxVersion</td><td>Maximum supported API version.</td><td>1 and later</td></tr></table>

## Example:

```json
{
    "data": {
    "SYNO.API.Auth": {
    "path": "auth.cgi",
    "minVersion": 1,
    "maxVersion": 3
    },
    "SYNO.FileStation.List": {
    "path": "FileStation/file_share.cgi",
    "minVersion": 1,
    "maxVersion": 1
    }
    },
    "success": true
} 
```

API Error Code 

No specific API error codes. 

# SYNO.API.Auth

Overview 

Availability: Since DSM 4.0 

Version: 3 (Since DSM 4.2), 2 (Since DSM 4.1) 

## Method

Login 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Availability</td></tr><tr><td>account</td><td>Login account name.</td><td>1 and later</td></tr><tr><td>passwd</td><td>Login account password.</td><td>1 and later</td></tr><tr><td>session</td><td>Login session name.</td><td>1 and later</td></tr><tr><td>format</td><td>Returned format of session ID. The following are the two possible options and the default value is cookie .cookie : The login session ID will be set to &quot;id&quot; key in cookie of HTTP/HTTPS header of response.sid : The login sid will only be returned as response JSON data and &quot;id&quot; key will not be set in cookie.</td><td>2 and later</td></tr><tr><td>otp_code</td><td>Reserved key. DSM 4.2 and later support a 2-step verification option with an OTP code. If it&#x27;s enabled, the user is required to enter a verification code to log in to DSM sessions. However, WebAPI doesn&#x27;t support it yet.</td><td>3 and later</td></tr></table>


Note: The applied sid will expire after 7 days by default. 


## Example:

GET /webapi/auth.cgi?api=SYNO.API.Auth&version=3&method=login&account=admin&passwd=12345&session=FileStation&format=cookie 

## Response:

<table><tr><td>Parameter</td><td>Description</td><td>Availability</td></tr><tr><td>sid</td><td>Authorized session ID. When the user log in with format=sid, cookie will not be set and each API request should provide a request parameter _sid=&lt;sid&gt; along with other parameters.</td><td>2 and later</td></tr></table>

## Example:

```json
{
    "sid":"xJoGn-S-K_Cx0zF_MA1xDhrQBVlAYTMBK6hxNHHsD6Y3CB8epaMRJ5JiHF7VHON5JV30lFwNPBU_tmQ_Ub1Q6M"
} 
```

<table><tr><td>Logout</td></tr></table>

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Availability</td></tr><tr><td>session</td><td>Session name to be logged out.</td><td>1 and later</td></tr></table>

## Example:

GET /webapi/auth.cgi?api=SYNO.API.Auth&version=1&method=logout&session=FileStation 

## Response:

No specific response. It returns an empty success response if completed without error. 

## API Error Code

<table><tr><td>Code</td><td>Description</td></tr><tr><td>400</td><td>No such account or incorrect password</td></tr><tr><td>401</td><td>Account disabled</td></tr><tr><td>402</td><td>Permission denied</td></tr><tr><td>403</td><td>2-step verification code required</td></tr><tr><td>404</td><td>Failed to authenticate 2-step verification code</td></tr></table>

## Chpater 4: File Station API

## API List

The following table is the overview of all File Station APIs defined in this chapter. All File Station APIs are required to login with SYNO.API.Auth and session=FileStation. 

<table><tr><td>API Name</td><td>Description</td></tr><tr><td>SYNO.FileStation.Info</td><td>Provide File Station info.</td></tr><tr><td>SYNO.FileStation.List</td><td>List all shared folders, enumerate files in a shared folder, and get detailed file information.</td></tr><tr><td>SYNO.FileStation.Search</td><td>Search files on given criteria.</td></tr><tr><td>SYNO.FileStation.VirtualFolder</td><td>List all mount point folders of virtual file system, e.g., CIFS or ISO.</td></tr><tr><td>SYNO.FileStation.Favorite</td><td>Add a folder to user&#x27;s favorites or perform operations on user&#x27;s favorites.</td></tr><tr><td>SYNO.FileStation.Thumb</td><td>Get a thumbnail of a file.</td></tr><tr><td>SYNO.FileStation.DirSize</td><td>Get the total size of files/folders within folder(s).</td></tr><tr><td>SYNO.FileStation.MD5</td><td>Get MD5 of a file.</td></tr><tr><td>SYNO.FileStation.CheckPermission</td><td>Check if the file/folder has permission of a file/folder or not.</td></tr><tr><td>SYNO.FileStation.Upload</td><td>Upload a file.</td></tr><tr><td>SYNO.FileStation.Download</td><td>Download files/folders.</td></tr><tr><td>SYNO.FileStation.Sharing</td><td>Generate a sharing link to share files/folders with other people and perform operations on sharing links.</td></tr><tr><td>SYNO.FileStation.CreateFolder</td><td>Create folder(s).</td></tr><tr><td>SYNO.FileStation.Rename</td><td>Rename a file/folder.</td></tr><tr><td>SYNO.FileStation.CopyMove</td><td>Copy/Move files/folders.</td></tr><tr><td>SYNO.FileStation.Delete</td><td>Delete files/folders.</td></tr><tr><td>SYNO.FileStation.Extract</td><td>Extract an archive and perform operations on an archive.</td></tr><tr><td>SYNO.FileStation.Compress</td><td>Compress files/folders.</td></tr><tr><td>SYNO.FileStation.BackgroundTask</td><td>Get information regarding tasks of file operations which are run as the background process including copy, move, delete, compress and extract tasks or perform operations on these background tasks.</td></tr></table>

## SYNO.FileStation.Info

## Description

Provide File Station information. 

## Overview

Availability: Since DSM 6.0 

Version: 2 

## Method

get 

Description: 

Provide File Station information. 

Request: 

No parameters are required. 

Example: 

GET /webapi/entry.cgi?api=SYNO.FileStation.Info&version=2&method=get 

Response: 

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>is_manager</td><td>Boolean</td><td>If the logged-in user is an administrator.</td><td>2 and later</td></tr><tr><td>support_virtual_protocol</td><td>String</td><td>Types of virtual file system which the logged user is able to mount on. DSM 6.0 supports CIFS, NFS and ISO of virtual file system. Different types are separated with a comma, for example: cifs,nfs,iso.</td><td>2 and later</td></tr><tr><td>support_sharing</td><td>Boolean</td><td>If the logged-in user can sharing file(s) / folder(s) or not.</td><td>2 and later</td></tr><tr><td>hostname</td><td>String</td><td>DSM host name.</td><td>2 and later</td></tr></table>

Example: 

```json
{
    "hostname": "test",
    "is_manager": true,
    "support_sharing": true,
    "support_virtual_protocol": "cifs,iso"
} 
```

## API Error Code

No specific API error codes. 

# SYNO.FileStation.List

## Description

List all shared folders, enumerate files in a shared folder, and get detailed file information. 

## Overview

Availability: Since DSM 6.0 

Version: 2 

## Method

list_share 

Description: 

List all shared folders. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>offset</td><td>Optional. Specify how many shared folders are skipped before beginning to return listed shared folders.</td><td>Integer</td><td>0</td><td>2 and later</td></tr><tr><td>limit</td><td>Optional. Number of shared folders requested. 0 lists all shared folders.</td><td>Integer</td><td>0</td><td>2 and later</td></tr><tr><td>sort_by</td><td>Optional. Specify which file information to sort on.Options include:name: file name.user: file owner.group: file group.mtime: last modified time.atime: last access time.ctime: last change time.crtime: create time.posix: POSIX permission.</td><td>name, user, group, mtime, atime, ctime, crtime or posix</td><td>name</td><td>2 and later</td></tr><tr><td>sort_direction</td><td>Optional. Specify to sort ascending or to sort descending.Options include:asc: sort ascending.desc: sort descending.</td><td>asc or desc</td><td>asc</td><td>2 and later</td></tr><tr><td>onlywritable</td><td>Optional.true: List writable shared folders.false: List writable and read-only shared folders.</td><td>true or false</td><td>false</td><td>2 and later</td></tr><tr><td>additional</td><td>Optional. Additional requested file information separated by a comma "," and around the brackets. When an additional option is requested, responded objects will be provided in the specified additional option. Options include: real_path: return a real path in volume.size: return file byte size.owner: return information about file owner including user name, group name, UID and GID.time: return information about time including last access time, last modified time, last change time and create time.perm: return information about file permission.mount_point_type: return a type of a virtual file system of a mount point.volume_status: return volume statuses including free space, total space and read-only status.</td><td>real_path, owner, time, perm, mount_point_type, sync_share, or volume_status</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.List&version=2&method=list_share&additional=%5B%22real_path%22%2C%22owner%2Ctime%22%5D 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>total</td><td>Integer</td><td>Total number of shared folders.</td><td>2 and later</td></tr><tr><td>offset</td><td>Integer</td><td>Requested offset.</td><td>2 and later</td></tr><tr><td>shares</td><td></td><td>Array ofobjects.</td><td>2 and later</td></tr></table>


<shared folder> object definition: 


<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>path</td><td>String</td><td>Path of a shared folder.</td><td>2 and later</td></tr><tr><td>name</td><td>String</td><td>Name of a shared folder.</td><td>2 and later</td></tr><tr><td>additional</td><td>&lt;shared-folder additional&gt; object</td><td>Shared-folder additional object.</td><td>2 and later</td></tr></table>


<shared-folder additional> object definition: 


<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>real_path</td><td>String</td><td>Real path of a shared folder in a volume space.</td><td>2 and later</td></tr><tr><td>owner</td><td>&lt;owner&gt; object</td><td>File owner information including user name, group name, UID and GID.</td><td>2 and later</td></tr><tr><td>time</td><td>&lt;time&gt; object</td><td>Time information of file including last access time, last modified time, last change time, and creation time.</td><td>2 and later</td></tr><tr><td>perm</td><td>&lt;shared-folder perm&gt; object</td><td>File permission information.</td><td>2 and later</td></tr><tr><td>mount_point_type</td><td>String</td><td>Type of a virtual file system of a mount point.</td><td>2 and later</td></tr><tr><td>volume_status</td><td>&lt;volume_status&gt; object</td><td>Volume status including free space, total space and read-only status.</td><td>2 and later</td></tr></table>

<owner> object definition: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>user</td><td>String</td><td>User name of file owner.</td><td>2 and later</td></tr><tr><td>group</td><td>String</td><td>Group name of file group.</td><td>2 and later</td></tr><tr><td>uid</td><td>Integer</td><td>File UID.</td><td>2 and later</td></tr><tr><td>gid</td><td>Integer</td><td>File GID.</td><td>2 and later</td></tr></table>

<time> object definition: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>atime</td><td>Linux timestamp in second</td><td>Linux timestamp of last access in second.</td><td>2 and later</td></tr><tr><td>mtime</td><td>Linux timestamp in second</td><td>Linux timestamp of last modification in second.</td><td>2 and later</td></tr><tr><td>ctime</td><td>Linux timestamp in second</td><td>Linux timestamp of last change in second.</td><td>2 and later</td></tr><tr><td>crtime</td><td>Linux timestamp in second</td><td>Linux timestamp of create time in second.</td><td>2 and later</td></tr></table>


Note: Linux timestamp in second, defined as the number of seconds that have elapsed since 00:00:00 Coordinated Universal Time (UTC), Thursday, 1 January 1970. 



<shared-folder perm> object definition: 


<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>share_right</td><td>String</td><td>&quot;RW&quot;: The shared folder is writable; &quot;RO&quot;: the shared folder is read-only.</td><td>2 and later</td></tr><tr><td>posix</td><td>Integer</td><td>POSIX file permission, For example, 777 means owner, group or other has all permission; 764 means owner has all permission, group has read/write permission, other has read permission.</td><td>2 and later</td></tr><tr><td>adv_right</td><td>object</td><td>Special privilege of the shared folder.</td><td>2 and later</td></tr><tr><td>acl_enable</td><td>Boolean</td><td>If Windows ACL privilege of the shared folder is enabled or not.</td><td>2 and later</td></tr><tr><td>is_acl_mode</td><td>Boolean</td><td>true: The privilege of the shared folder is set to be ACL-mode. false: The privilege of the shared folder is set to be POSIX-mode.</td><td>2 and later</td></tr><tr><td>acl</td><td>object</td><td>Windows ACL privilege. If a shared folder is set to be POSIX-mode, these values of Windows ACL privileges are derived from the POSIX privilege.</td><td>2 and later</td></tr></table>


<adv_right> object definition:


<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>disable_download</td><td>Boolean</td><td>If a non-administrator user can download files in this shared folder through SYNO.FileStation.Download API or not.</td><td>2 and later</td></tr><tr><td>disable_list</td><td>Boolean</td><td>If a non-administrator user can enumerate files in this shared folder though SYNO.FileStation.List API with list method or not.</td><td>2 and later</td></tr><tr><td>disable_modify</td><td>Boolean</td><td>If a non-administrator user can modify or overwrite files in this shared folder or not.</td><td>2 and later</td></tr></table>


<acl> object definition:


<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>append</td><td>Boolean</td><td>If a logged-in user has a privilege to append data or create folders within this folder or not.</td><td>2 and later</td></tr><tr><td>del</td><td>Boolean</td><td>If a logged-in user has a privilege to delete a file/a folder within this folder or not.</td><td>2 and later</td></tr><tr><td>exec</td><td>Boolean</td><td>If a logged-in user has a privilege to execute files/traverse folders within this folder or not.</td><td>2 and later</td></tr><tr><td>read</td><td>Boolean</td><td>If a logged-in user has a privilege to read data or list folder within this folder or not.</td><td>2 and later</td></tr><tr><td>write</td><td>Boolean</td><td>If a logged-in user has a privilege to write data or create files within this folder or not.</td><td>2 and later</td></tr></table>


<volume_status> object definition: 


<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>freespace</td><td>Integer</td><td>Byte size of free space of a volume where a shared folder is located.</td><td>2 and later</td></tr><tr><td>totalspace</td><td>Integer</td><td>Byte size of total space of a volume where a shared folder is located.</td><td>2 and later</td></tr><tr><td>readonly</td><td>Boolean</td><td>true: A volume where a shared folder is located is read-only; false: It&#x27;s writable.</td><td>2 and later</td></tr></table>

## Example:

```json
{
    "shares": [
    {
    "isdir": true,
    "name": "video",
    "path": "/video",
    "additional": {
    "owner": {
    "gid": 100,
    "group": "users",
    "uid": 1024,
    "user": "admin"
    },
    "real_path": "/volume1/video",
    "time": {
    "atime": 1374918626,
    "crtime": 1363259974,
    "ctime": 1371713685,
    "mtime": 1371713685
    }
    }
},
{
    "isdir": true,
    "name": "photo",
    "path": "/photo",
    "additional": {
    "owner": {
    "gid": 100,
    "group": "users",
    "uid": 1024,
    "user": "admin"
    },
    "real_path": "/volume1/photo",
    "time": {
    "atime": 1371630215,
    "crtime": 1352168821,
    "ctime": 1368769689,
    "mtime": 1368769689
    }
    }
}
],
"offset": 0,
"total": 2
} 
```

## list

Description: 

Enumerate files in a given folder. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>folder_path</td><td>A listed folder path starting with a shared folder.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>offset</td><td>Optional. Specify how many files are skipped before beginning to return listed files.</td><td>Integer</td><td>0</td><td>2 and later</td></tr><tr><td>limit</td><td>Optional. Number of files requested. 0 indicates to list all files with a given folder.</td><td>Integer</td><td>0</td><td>2 and later</td></tr><tr><td>sort_by</td><td>Optional. Specify which file information to sort on.Options include:name: file name.size: file size.user: file owner.group: file group.mtime: last modified time.atime: last access time.ctime: last change time.crtime: create time.posix: POSIX permission.type: file extension.</td><td>name, size, user, group, mtime, atime, ctime, crtime, posix or type</td><td>name</td><td>2 and later</td></tr><tr><td>sort_direction</td><td>Optional. Specify to sort ascending or to sort descending.Options include:asc: sort ascending.desc: sort descending.</td><td>asc or desc</td><td>asc</td><td>2 and later</td></tr><tr><td>pattern</td><td>Optional. Given glob pattern(s) to find files whose names and extensions match a case-insensitive glob pattern.Note:1. If the pattern doesn't contain any glob syntax (? and *), * of glob syntax will be added at begin and end of the string automatically for partially matching the pattern.2. You can use "," to separate multiple glob patterns.**</td><td>Glob patterns</td><td>(None)</td><td>2 and later</td></tr><tr><td>filetype</td><td>Optional. "file": only enumerate regular files; "dir": only enumerate folders; "all" enumerate regular files and folders.</td><td>file, dir or all</td><td>all</td><td>2 and later</td></tr><tr><td>goto_path</td><td>Optional. Folder path starting with a shared folder. Return all files and sub-folders within folder_path path until goto_path path recursively.Note: goto_path is only valid with parameter "additional" contains real_path.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>additional</td><td>Optional. Additional requested file information separated by a comma "," and around the brackets. When an additional option is requested, responded objects will be provided in the specified additional option. Options include: real_path: return a real path in volume.size: return file byte size.owner: return information about file owner including user name, group name, UID and GID.time: return information about time including last access time, last modified time, last change time and create time.perm: return information about file permission.mount_point_type:return a type of a virtual file system of a mount point.type: return a file extension.</td><td>real_path, size, owner, time, perm, type or mount_point_type</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.List&version=2&method=list&additional=%5B%22real_path%22%2C%22size%22%2C%22owner%22%2C%22time%2Cperm%22%2C%22type%22%5D&folder_path=%22%2Fvideo%22 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>total</td><td>Integer</td><td>Total number of files.</td><td>2 and later</td></tr><tr><td>offset</td><td>Integer</td><td>Requested offset.</td><td>2 and later</td></tr><tr><td>files</td><td></td><td>Array ofobjects.</td><td>2 and later</td></tr></table>


<file> object definition: 


<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>path</td><td>String</td><td>Folder/file path starting with a shared folder.</td><td>2 and later</td></tr><tr><td>name</td><td>String</td><td>File name.</td><td>2 and later</td></tr><tr><td>isdir</td><td>Boolean</td><td>If this file is a folder or not.</td><td>2 and later</td></tr><tr><td>children</td><td>object</td><td>File list within a folder which is described by aobject. The value is returned, only if goto_path parameter is given.</td><td>2 and later</td></tr><tr><td>additional</td><td>object</td><td>File additional object.</td><td>2 and later</td></tr></table>

<children> object definition: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>total</td><td>Integer</td><td>Total number of files.</td><td>2 and later</td></tr><tr><td>offset</td><td>Integer</td><td>Requested offset.</td><td>2 and later</td></tr><tr><td>files</td><td></td><td>Array ofobjects.</td><td>2 and later</td></tr></table>


<file additional> object definition:


<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>real_path</td><td>String</td><td>Real path starting with a volume path.</td><td>2 and later</td></tr><tr><td>size</td><td>Integer</td><td>File size.</td><td>2 and later</td></tr><tr><td>owner</td><td>object</td><td>File owner information including user name, group name, UID and GID.</td><td>2 and later</td></tr><tr><td>time</td><td>object</td><td>Time information of file including last access time, last modified time, last change time and create time.</td><td>2 and later</td></tr><tr><td>perm</td><td>object</td><td>File permission information.</td><td>2 and later</td></tr><tr><td>mount_point_type</td><td>String</td><td>A type of a virtual file system of a mount point.</td><td>2 and later</td></tr><tr><td>type</td><td>String</td><td>File extension.</td><td>2 and later</td></tr></table>


<owner> object definition: 


<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>user</td><td>String</td><td>User name of file owner.</td><td>2 and later</td></tr><tr><td>group</td><td>String</td><td>Group name of file group.</td><td>2 and later</td></tr><tr><td>uid</td><td>Integer</td><td>File UID.</td><td>2 and later</td></tr><tr><td>gid</td><td>Integer</td><td>File GID.</td><td>2 and later</td></tr></table>


<time> object definition: 


<acl> object definition: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>atime</td><td>Linux timestamp in second</td><td>Linux timestamp of last access in second.</td><td>2 and later</td></tr><tr><td>mtime</td><td>Linux timestamp in second</td><td>Linux timestamp of last modification in second.</td><td>2 and later</td></tr><tr><td>ctime</td><td>Linux timestamp in second</td><td>Linux timestamp of last change in second.</td><td>2 and later</td></tr><tr><td>crtime</td><td>Linux timestamp in second</td><td>Linux timestamp of create time in second.</td><td>2 and later</td></tr></table>


Note: Linux timestamp, defined as the number of seconds that have elapsed since 00:00:00 Coordinated Universal Time (UTC), Thursday, 1 January 1970.



<perm> object definition:


<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>posix</td><td>Integer</td><td>POSIX file permission. For example, 777 means owner, group or other has all permission; 764 means owner has all permission, group has read/write permission, other has read permission.</td><td>2 and later</td></tr><tr><td>is_acl_mode</td><td>Boolean</td><td>true: the privilege of the shared folder is set to be ACL-mode; false: the privilege of the shared folder is set to be POSIX-mode.</td><td>2 and later</td></tr><tr><td>acl</td><td>Object</td><td>Windows ACL privilege. If a file is set to be POSIX-mode, these values of Windows ACL privilege are derived from the POSIX privilege.</td><td>2 and later</td></tr></table>

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>append</td><td>Boolean</td><td>If a logged-in user has a privilege to append data or create folders within this folder or not.</td><td>2 and later</td></tr><tr><td>del</td><td>Boolean</td><td>If a logged-in user has a privilege to delete a file/a folder within this folder or not.</td><td>2 and later</td></tr><tr><td>exec</td><td>Boolean</td><td>If a logged-in user has a privilege to execute files or traverse folders within this folder or not.</td><td>2 and later</td></tr><tr><td>read</td><td>Boolean</td><td>If a logged-in user has a privilege to read data or list folder within this folder or not.</td><td>2 and later</td></tr><tr><td>write</td><td>Boolean</td><td>If a logged-in user has a privilege to write data or create files within this folder or not.</td><td>2 and later</td></tr></table>


Example: 


```json
{
    "files": [
    {
    "additional": {
    "owner": {
    "gid": 100,
    "group": "users",
    "uid": 1024,
    "user": "admin"
    },
    "perm": {
    "acl": {
    "append": true,
    "del": true,
    "exec": true,
    "read": true,
    "write": true
    },
    "is_acl_mode": false,
    "posix": 777
    },
    "real_path": "/volume1/video/1",
    "size": 4096,
    "time": {
    "atime": 1370104559,
    "crtime": 1370104559,
    "ctime": 1370104559,
    "mtime": 1369728913
    },
    "type": ""
    },
    "isdir": true,
    "name": "1",
    "path": "/video/1"
    },
    {
    "additional": {
    "owner": {
    "gid": 100,
    "group": "users",
    "uid": 1024,
    "user": "admin"
    },
    "perm": {
    "acl": {
    "append": true,
    "del": true,
    "exec": true,
    "read": true,
    "write": true
    },
    "is_acl_mode": false,
    " posix": 777
    },
    "real_path": "/volume1/video/2.txt",
    "size": 12800,
    "time": {
    "atime": 1369964337,
    "crtime": 1369964337,
    "ctime": 1372410504,
    "mtime": 1369964408
    },
    "type": "TXT"
    },
    "isdir": false, 
```

```json
"name": "2.txt",
"path": "/video/2.txt"
}
],
"offset": 0,
"total": 2
} 
```

## getinfo

Description: 

Get information of file(s). 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>One or more folder/file path(s) starting with a shared folder, separated by a comma &quot;,&quot; and around backets.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>additional</td><td>Optional. Additional requested file information, separated by a comma &quot;,&quot; and around the brackets. When an additional option is requested, responded objects will be provided in the specified additional option. Options include: real_path: return a real path in volume.size: return file byte size.owner: return information about file owner including user name, group name, UID and GID.time: return information about time including last access time, last modified time, last change time and create time.perm: return information about file permission.mount_point_type: return a type of a virtual file system of a mount point.type: return a file extension</td><td>real_path, size, owner, time, perm, type or mount_point_type&lt;/li&gt;</td><td>(None)</td><td>2 and later</td></tr></table>

Example: 

GET /webapi/entry.cgi?api=SYNO.FileStation.List&version=2&method=getinfo+additional=%5B%22real_path%22%2C%22size%2Cowner%22%2C%22time%2Cperm%2C%22type%22%5D&path=%5B%22%2Fvideo%2F1%22%2C%22%2Fvideo%2F2.txt%22%5D 

Response: 


<data> object definitions:


<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>files</td><td></td><td>Array ofobjects.</td><td>2 and later</td></tr></table>

<file> object definition: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>path</td><td>String</td><td>Folder/file path starting with a shared folder.</td><td>2 and later</td></tr><tr><td>name</td><td>String</td><td>File name.</td><td>2 and later</td></tr><tr><td>isdir</td><td>Boolean</td><td>If this file is a folder or not.</td><td>2 and later</td></tr><tr><td>additional</td><td>object</td><td>File additional object.</td><td>2 and later</td></tr></table>


<file additional> object definition: Same as definition in the list method. 


Example: 

```json
{
    "files": [
    {
    "additional": {
    "owner": {
    "gid": 100,
    "group": "users",
    "uid": 1024,
    "user": "admin"
    },
    "perm": {
    "acl": {
    "append": true,
    "del": true,
    "exec": true,
    "read": true,
    "write": true
    },
    "is_acl_mode": false,
    "posix": 777
    },
    "real_path": "/volume1/video/1",
    "size": 4096,
    "time": {
    "atime": 1370104559,
    "crtime": 1370104559,
    "ctime": 1370104559,
    "mtime": 1369728913
    },
    "type": ""
    },
    "isdir": true,
    "name": "1",
    "path": "/video/1"
    },
    {
    "additional": {
    "owner": {
    "gid": 100,
    "group": "users",
    "uid": 1024,
    "user": "admin"
    },
    "perm": {
    "acl": {
    "append": true,
    "del": true,
    "exec": true,
    "read": true,
    "write": true
    },
    "is_acl_mode": false,
    " posix": 777
    },
    "real_path": "/volume1/video/2.txt",
    "size": 12800,
    "time": {
    "atime": 1369964337,
    "crtime": 1369964337,
    "ctime": 1372410504,
    "mtime": 1369964408
    },
    "type": "TXT"
    },
    "isdir": false, 
```

## API Error Code

No specific API error codes. 

# SYNO.FileStation.Search

## Description

Search files according to given criteria. 

This is a non-blocking API. You need to start to search files with the start method. Then, you should poll requests with list method to get more information, or make a request with the stop method to cancel the operation. Otherwise, search results are stored in a search temporary database so you need to call clean method to delete it at the end of operation. 

## Overview

Availability: Since DSM 6.0 

Version: 2 

## Method

## start

Description: 

Start to search files according to given criteria. If more than one criterion is given in different parameters, searched files match all these criteria. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>folder_path</td><td>A searched folder path starting with a shared folder. One or more folder paths to be searched, separated by commas "," and around brackets.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>recursive</td><td>Optional. If searching files within a folder and subfolders recursively or not.</td><td>Boolean</td><td>true</td><td>2 and later</td></tr><tr><td>pattern</td><td>Optional. Search for files whose names and extensions match a case-insensitive glob pattern.Note:1. If the pattern doesn't contain any glob syntax (? and *), * of glob syntax will be added at begin and end of the string automatically for partially matching the pattern.2. You can use "" to separate multiple glob patterns.</td><td>Glob patterns</td><td>(None)</td><td>2 and later</td></tr><tr><td>extension</td><td>Optional. Search for files whose extensions match a file type pattern in a case-insensitive glob pattern. If you give this criterion, folders aren't matched.Note: You can use commas "," to separate multiple glob patterns.</td><td>Glob patterns</td><td>(None)</td><td>2 and later</td></tr><tr><td>filetype</td><td>Optional. "file": enumerate regular files; "dir": enumerate folders; "all" enumerate regular files and folders.</td><td>file, dir or all</td><td>all</td><td>2 and later</td></tr><tr><td>size_from</td><td>Optional. Search for files whose sizes are greater than the given byte size.</td><td>Byte size</td><td>(None)</td><td>2 and later</td></tr><tr><td>size_to</td><td>Optional. Search for files whose sizes are less than the given byte size.</td><td>Byte size</td><td>(None)</td><td>2 and later</td></tr><tr><td>mtime_from</td><td>Optional. Search for files whose last modified time after the given Linux timestamp in second.</td><td>Linux timestamp in second</td><td>(None)</td><td>2 and later</td></tr><tr><td>mtime_to</td><td>Optional. Search for files whose last modified time before the given Linux timestamp in second.</td><td>Linux timestamp in second</td><td>(None)</td><td>2 and later</td></tr><tr><td>crtime_from</td><td>Optional. Search for files whose create time after the given Linux timestamp in second.</td><td>Linux timestamp in second</td><td>(None)</td><td>2 and later</td></tr><tr><td>crtime_to</td><td>Optional. Search for files whose create time before the given Linux timestamp in second.</td><td>Linux timestamp in second</td><td>(None)</td><td>2 and later</td></tr><tr><td>atime_from</td><td>Optional. Search for files whose last access time after the given Linux timestamp in second.</td><td>Linux timestamp in second</td><td>(None)</td><td>2 and later</td></tr><tr><td>atime_to</td><td>Optional. Search for files whose last access time before the given Linux timestamp in second.</td><td>Linux timestamp in second</td><td>(None)</td><td>2 and later</td></tr><tr><td>owner</td><td>Optional. Search for files whose user name matches this criterion. This criterion is case-insensitive.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>group</td><td>Optional. Search for files whose group name matches this criterion. This criterion is case-insensitive.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

Note: Linux timestamp in second, defined as the number of seconds that have elapsed since 00:00:00 Coordinated Universal Time (UTC), Thursday, 1 January 1970. 

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Search&version=2&method=start&folder_path=%5B%22%2Fvideo%22%5D&pattern="1" 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>task id</td><td>String</td><td>A unique ID for the search task</td><td>2 and later</td></tr></table>

## Example:

```json
{
    "task id": "51CE617CF57B24E5"
} 
```

## list

## Description:

List matched files in a search temporary database. You can check the finished value in response to know if the search operation is processing or has been finished. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>taskid</td><td>A unique ID for the search task which is obtained from start method.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>offset</td><td>Optional. Specify how many matched files are skipped before beginning to return listed matched files.</td><td>Integer</td><td>0</td><td>2 and later</td></tr><tr><td>limit</td><td>Optional. Number of matched files requested. -1 indicates to list all matched files. 0 indicates to list nothing.</td><td>Integer</td><td>0</td><td>2 and later</td></tr><tr><td>sort_by</td><td>Optional. Specify which file information to sort on. Options include: name: file name. size: file size. user: file owner. group: file group. mtime: last modified time. atime: last access time. ctime: last change time. crtime: create time. posix: POSIX permission. type: file extension.</td><td>name, size, user, group, mtime, atime, ctime, crtime, posix or type</td><td>name</td><td>2 and later</td></tr><tr><td>sort_direction</td><td>Optional. Specify to sort ascending or to sort descending. Options include: asc: sort ascending. desc: sort descending.</td><td>asc or desc</td><td>asc</td><td>2 and later</td></tr><tr><td>pattern</td><td>Optional. Given glob pattern(s) to find files whose names and extensions match a case-insensitive glob pattern. Note: 1. If the pattern doesn't contain any glob syntax (? and *), * of glob syntax will be added at begin and end of the string automatically for partially matching the pattern. 2. You can use " " to separate multiple glob patterns.</td><td>Glob patterns</td><td>String</td><td>2 and later</td></tr><tr><td>filetype</td><td>Optional. "file": enumerate regular files; "dir": enumerate folders; "all" enumerate regular files and folders.</td><td>file, dir or all</td><td>all</td><td>2 and later</td></tr><tr><td>additional</td><td>Optional. Additional requested file information separated by a comma "," and around the brackets. When an additional option is requested, responded objects will be provided in the specified additional option. Options include: real_path: return a real path in volume.size: return file byte size.owner: returns information about file owner including user name, group name, UID and GID.time: return information about time including last access time, last modified time, last change time and create time.perm: return information about file permission.type: return a file extension.</td><td>real_path, size, owner, time, perm or type</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Search&version=2&method=list&
additional=%5B%22real_path%22%2C%22size%22%2C%22owner%22%2C%22time%22%2C%22perm%22%2C%22type%22%5D&taskid=%2251CE617CF57B24E5%22&limit=-1 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>total</td><td>Integer</td><td>Total number of matched files.</td><td>2 and later</td></tr><tr><td>offset</td><td>Integer</td><td>Requested offset.</td><td>2 and later</td></tr><tr><td>finished</td><td>Boolean</td><td>If the searching task is finished or not.</td><td>2 and later</td></tr><tr><td>files</td><td></td><td>Array ofobjects.</td><td>2 and later</td></tr></table>


<file> object definitions: 


Same as definition in the list method of SYNO.FileStation.List API 

Example: 

```json
{
    "files": [
    {
    "additional": {
    "owner": {
    "gid": 100,
    "group": "users",
    "uid": 1024,
    "user": "admin"
    },
    "perm": {
    "acl": {
    "append": true,
    "del": true,
    "exec": true,
    "read": true,
    "write": true
    },
    "is_acl_mode": false,
    "posix": 644
    },
    "real_path": "/volume1/video/12",
    "size": 0,
    "time": {
    "atime": 1370002059,
    "crtime": 1370002059,
    "ctime": 1370002099,
    "mtime": 1370002059
    },
    "type": ""
    },
    "isdir": false,
    "name": "12",
    "path": "/video/12"
    },
    {
    "additional": {
    "owner": {
    "gid": 100,
    "group": "users",
    "uid": 1024,
    "user": "admin"
    },
    "perm": {
    "acl": {
    "append": true,
    "del": true,
    "exec": true,
    "read": true,
    "write": true
    },
    "is_acl_mode": true,
    "posix": 70
    },
    "real_path": "/volume1/video/1GFILE.txt",
    "size": 1073741825,
    "time": {
    "atime": 1370522981,
    "crtime": 1370522690,
    "ctime": 1370522815,
    "mtime": 1370522814
    },
    "type": "TXT"
    },
    "isdir": false, 
```

```jsonl
"name": "1GFILE.txt",
    "path": "/video/1GFILE.txt"
},
"finished": true,
"offset": 0,
"total": 2
} 
```

stop 

## Description:

Stop the searching task(s). The search temporary database won't be deleted, so it's possible to list the search result using list method after stopping it. 

Availability: 

Since version 2. 


Request:


<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>Unique ID(s) for the search task which are obtained from start method. Specify multiple search task IDs separated by a comma &quot;,&quot; and around the brackets.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Search&version=1&method=stop&taskid=%2251CE617CF57B24E5%22 

## Response:

No specific response. It returns an empty success response if completed without error. 

clean 

Description: 

Delete search temporary database(s). 

Availability: 

Since version 1. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>Unique ID(s) for the search task which are obtained from start method. Specify multiple search task IDs separated by a comma &quot;,&quot; and around the brackets.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

GET /webapi/entry.cgi?api=SYNO.FileStation.Search&version=1&method=clean&taskid=%2251CE617CF57B24E5%22 

## Response:

No specific response. It returns an empty success response if completed without error. 

## API Error Code

No specific API error codes. 

# SYNO.FileStation.VirtualFolder

## Description

List all mount point folders of virtual file system, e.g., CIFS or ISO. 

## Overview

Availability: Since DSM 6.0 

Version: 2 

## Method

## list

Description: 

List all mount point folders on one given type of virtual file system. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>type</td><td>A type of virtual file systems, e.g., NFS, CIFS or ISO.</td><td>Nfs, cifs or iso</td><td>(None)</td><td>2 and later</td></tr><tr><td>offset</td><td>Optional. Specify how many mount point folders are skipped before beginning to return listed mount point folders in virtual file system.</td><td>Integer</td><td>0</td><td>2 and later</td></tr><tr><td>limit</td><td>Optional. Number of mount point folders requested. 0 indicates to list all mount point folders in virtual file system.</td><td>Integer</td><td>0</td><td>2 and later</td></tr><tr><td>sort_by</td><td>Optional. Specify which file information to sort on.Options include:name: file name.user: file owner.group: file group.mtime: last modified time.atime: last access time.ctime: last change time.crtime: create time.posix: POSIX permission.</td><td>name, user, group, mtime, atime, ctime, crtime or posix</td><td>Name</td><td>2 and later</td></tr><tr><td>sort_direction</td><td>Optional. Specify to sort ascending or to sort descending.Options include:asc: sort ascending.desc: sort descending.</td><td>asc or desc</td><td>asc</td><td>2 and later</td></tr><tr><td>additional</td><td>Optional. Additional requested file information separated by a comma &quot;,&quot; and around the brackets. When an additional option is requested, responded objects will be provided in the specified additional option.Options include:real_path: return a real path in volume.size: return file byte size.owner: return information about file owner including user name, group name, UID and GID.time: return information about time including last access time, last modified time, last change time and create time.perm: return information about file permission.volume_status: return information about volume status including free space, total space and read-only status.</td><td>real_path, owner, time, perm, mount_point_type or volume_status</td><td>(None)</td><td>2 and later</td></tr></table>


Example: 


GET /webapi/entry.cgi?api=SYNO.FileStation.VirtualFolder&version=2&method=list&type=%22cifs%22&additional=%5B%22real_path%22%2C%22owner%22%2C%22time%22%2C%22perm%22%2C%22mount_point_type%22%2C%22volume_status%22%5D 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>total</td><td>Integer</td><td>Total number of mount point folders.</td><td>2 and later</td></tr><tr><td>offset</td><td>Integer</td><td>Requested offset.</td><td>2 and later</td></tr><tr><td>folders</td><td></td><td>Array ofobject.</td><td>2 and later</td></tr></table>

<virtual folder> object definition: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>path</td><td>String</td><td>Path of a mount point folder.</td><td>2 and later</td></tr><tr><td>name</td><td>String</td><td>Name of a mount point folder.</td><td>2 and later</td></tr><tr><td>additional</td><td>object</td><td>Virtual folder additional object.</td><td>2 and later</td></tr></table>

<virtual-folder additional> object definition: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>real_path</td><td>String</td><td>Real path starting with a volume path.</td><td>2 and later</td></tr><tr><td>owner</td><td>object</td><td>File owner information including user name, group name, UID and GID.</td><td>2 and later</td></tr><tr><td>time</td><td>object</td><td>Time information of file including last access time, last modified time, last change time and create time.</td><td>2 and later</td></tr><tr><td>perm</td><td>object</td><td>File permission information.</td><td>2 and later</td></tr><tr><td>mount_point_type</td><td>String</td><td>A type of a virtual file system of a mount point.</td><td>2 and later</td></tr><tr><td>volume_status</td><td>object</td><td>Volume status including free space, total space and read-only status.</td><td>2 and later</td></tr></table>

<owner>, <time> and <perm> object definition: Same as definition in the list method of SYNO.FileStation.List API. 

<volume_status> object definition: Same as definition in the list_share method of SYNO.FileStation.List API. 

Example: 

```json
{
    "folders": [
    {
    "additional": {
    "mount_point_type": "remote",
    "owner": {
    "gid": 100,
    "group": "users",
    "uid": 1024,
    "user": "admin"
    },
    "perm": {
    "acl": {
    "append": true,
    "del": true,
    "exec": true,
    "read": true,
    "write": true
    },
    "is_acl_mode": false,
    "posix": 777
    },
    "real_path": "/volume1/vidoe/remote",
    "time": {
    "atime": 1372313445,
    "crtime": 1320204670,
    "ctime": 1371206944,
    "mtime": 1371206944
    },
    "volume_status": {
    "freespace": 12282422599680,
    "readonly": false,
    "totalspace": 801958928384
    }
    },
    "isdir": true,
    "name": "remote",
    "path": "/video/remote"
    }
],
"offset": 0,
"total": 1
} 
```

## API Error Code

No specific API error codes. 

# SYNO.FileStation.Favorite

## Description

Add a folder to user's favorites or perform operations on user's favorites. 

## Overview

Availability: Since DSM 6.0 

Version: 2 

## Method

list 

Description: 

List user's favorites. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>offset</td><td>Optional. Specify how many favorites are skipped before beginning to return user&#x27;s favorites.</td><td>Integer</td><td>0</td><td>2 and later</td></tr><tr><td>limit</td><td>Optional. Number of favorites requested. 0 indicates to list all favorites.</td><td>Integer</td><td>0</td><td>2 and later</td></tr><tr><td>status_filter</td><td>Optional. Show favorites with a given favorite status. Options of favorite statuses include:valid:A folder which a favorite links to exists.broken:A folder which a favorite links to doesn&#x27;t exist or isn&#x27;t permitted to access it.all: Both valid and broken statuses.</td><td>valid, broken or all</td><td>all</td><td>2 and later</td></tr><tr><td>additional</td><td>Optional. Additional requested information of a folder which a favorite links to separated by a comma &quot;,&quot; and around the brackets. When an additional option is requested, responded objects will be provided in the specified additional option. Options include:real_path:return a real path in volume.owner:return information about file owner including user name, group name, UID and GID.time:return information about time including last access time, last modified time, last change time and create time.perm:return information about file permission.mount_point_type:return a type of a virtual file system of a mount point.</td><td>name, size, user, group, mtime, atime, ctime, crtime, posix or type</td><td>name</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Favorite&version=1&method=list 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>total</td><td>Integer</td><td>Total number of favorites.</td><td>2 and later</td></tr><tr><td>offset</td><td>Integer</td><td>Requested offset.</td><td>2 and later</td></tr><tr><td>favorites</td><td></td><td>Array ofobjects.</td><td>2 and later</td></tr></table>


<favorite> object definition: 


<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>path</td><td>String</td><td>Folder path of a user&#x27;s favorites, starting with a shared folder.</td><td>2 and later</td></tr><tr><td>name</td><td>String</td><td>Favorite name.</td><td>2 and later</td></tr><tr><td>status</td><td>String</td><td>Favorite status. Values of favorite status include:valid:A folder, which a favorite links to, exists.broken:A folder, which a favorite links to, doesn&#x27;t exist or is not permitted to access it.</td><td>2 and later</td></tr><tr><td>additional</td><td>object</td><td>Favorite additional object.</td><td>2 and later</td></tr></table>

<favorite additional> object definition: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>real_path</td><td>String</td><td>Real path starting with a volume path.</td><td>2 and later</td></tr><tr><td>owner</td><td>object</td><td>File owner information including user name, group name, UID and GID.</td><td>2 and later</td></tr><tr><td>time</td><td>object</td><td>Time information of file including last access time, last modified time, last change time and create time.</td><td>2 and later</td></tr><tr><td>perm</td><td>object</td><td>File permission information.</td><td>2 and later</td></tr><tr><td>mount_point_type</td><td>String</td><td>A type of a virtual file system of a mount point.</td><td>2 and later</td></tr><tr><td>type</td><td>String</td><td>File extension.</td><td>2 and later</td></tr></table>


<owner>, <time>, <perm> object definition: Same as definition in the list method of SYNO.FileStation.List API. 


## Example:

```json
{
    "favorites": [
    {
    "isdir": true,
    "name": "My Video Shared folder",
    "path": "/video",
    "status": "valid"
    },
    {
    "isdir": false,
    "name": "deletedfolder",
    "path": "/share/deletedfolder",
    "status": "broken"
    }
    ],
    "offset": 0,
    "total": 2
} 
```

## add

Description: 

Add a folder to user's favorites. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>A folder path starting with a shared folder is added to the user&#x27;s favorites.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>name</td><td>A favorite name.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>index</td><td>Optional. Index of location of an added favorite. If it&#x27;s equal to -1, the favorite will be added to the last one in user&#x27;s favorite. If it&#x27;s between 0 ~ total number of favorites-1, the favorite will be inserted into user&#x27;s favorites by the index.</td><td>Integer</td><td>-1</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Favorite&version=2&method=add&path=%22%2Fvideo%2Ffav%22&name=%22favorite_video%22 

Response: 

No specific response. It returns an empty success response if completed without error. 

<table><tr><td>delete</td></tr></table>

Description: 

Delete a favorite in user's favorites. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>A folder path starting with a shared folder is deleted from a user&#x27;s favorites.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/FileStation/entry.cgi?api=SYNO.FileStation.Favorite&version=2&method=delete&path=%22%2Fvideo%2Ffav%22 

## Response:

No specific response. It returns an empty success response if completed without error. 

## clear_broken

Description: 

Delete all broken statuses of favorites. 

Availability: 

Since version 2. 

Request: 

No parameters are required. 

Example: 

GET /webapi/entry.cgi?api=SYNO.FileStation.Favorite&version=2&method=clear_broken 

Response: 

No specific response. It returns an empty success response if completed without error. 

edit 

Description: 

Edit a favorite name. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>A folder path starting with a shared folder is edited from a user&#x27;s favorites.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>name</td><td>New favorite name.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/FileStation/entry.cgi?api=SYNO.FileStation.Favorite&version=2&methodedit&path=%22%2Fvideo%2Ffav%22&name=%22my_video%22 

## Response:

No specific response. It returns an empty success response if completed without error. 

replace_all 

Description: 

Replace multiple favorites of folders to the existing user's favorites. 

## Availability:

Since version 2. 


Request:


<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>Path</td><td>One or more folder paths starting with a shared folder, separated by a comma &quot;,&quot; and around the brackets is added to the user&#x27;s favorites. The number of paths must be the same as the number of favorite names in the name parameter. The first path parameter corresponds to the first name parameter.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>Name</td><td>One or more new favorite names, separated by a comma &quot;,&quot; and around the brackets. The number of favorite names must be the same as the number of folder paths in the path parameter. The first name parameter corresponding to the first path parameter.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Favorite&version=1&method=replace_all&path=%5B%22%2Fvideo%22%2C%22%2Fvideo%2Ffav%22%5D&name=%5B%22all_video%22%2C%22my_video%22%5D 

## Response:

No specific response. It returns an empty success response if completed without error. 

## API Error Code

<table><tr><td>Code</td><td>Description</td></tr><tr><td>800</td><td>A folder path of favorite folder is already added to user&#x27;s favorites.</td></tr><tr><td>801</td><td>A name of favorite folder conflicts with an existing folder path in the user&#x27;s favorites.</td></tr><tr><td>802</td><td>There are too many favorites to be added.</td></tr></table>

# SYNO.FileStation.Thumb

## Description

Get a thumbnail of a file. 

## Note:

1. Supported image formats: jpg, jpeg, jpe, bmp, png, tif, tiff, gif, arw, srf, sr2, dcr, k25, kdc, cr2, crw, nef, mrw, ptx, pef, raf, 3fr, erf, mef, mos, orf, rw2, dng, x3f, heic, raw. 

2. Supported video formats in an indexed folder: 3gp, 3g2, asf, dat, divx, dvr-ms, m2t, m2ts, m4v, mkv, mp4, mts, mov, qt, tp, trp, ts, vob, wmv, xvid, ac3, amr, rm, rmvb, ifo, mpeg, mpg, mpe, m1v, m2v, mpeg1, mpeg2, mpeg4, ogv, webm, flv, f4v, avi, swf, vdr, iso, hevc. 

3. Video thumbnails exist only if video files are placed in the "photo" shared folder or users' home folders. 

## Overview

Availability: Since DSM 6.0 

Version: 2 

## Method

## get

Description: 

Get a thumbnail of a file. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>A file path starting with a shared folder.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>size</td><td>Optional. Return different size thumbnail.Size Options:small: small-size thumbnail.medium: medium-size thumbnail.large: large-size thumbnail.original: original-size thumbnail.</td><td>small, medium, large or original</td><td>small</td><td>2 and later</td></tr><tr><td>rotate</td><td>Optional. Return rotated thumbnail.Rotate Options:0: Do not rotate.1: Rotate 90°.2: Rotate 180°.3: Rotate 270°.4: Rotate 360°.</td><td>0, 1, 2, 3, 4</td><td>0</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation pulp&version=2&method=get&path=%22%2Fphoto%2Ftest.jpg%22 

Response: 

Image binary data. 

## API Error Code

Standard HTTP status codes. 

For example, 404 Not Found. 

# SYNO.FileStation.DirSize

## Description

Get the accumulated size of files/folders within folder(s). 

This is a non-blocking API. You need to start it with the start method. Then, you should poll requests with the status method to get progress status or make a request with stop method to cancel the operation. 

## Overview

Availability: Since DSM 6.0 

Version: 2 

## Method

start 

Description: 

Start to calculate size for one or more file/folder paths. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>One or more file/folder paths starting with a shared folder for calculating cumulative size, separated by a comma &quot;,&quot; and around the brackets.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.DirSize&version=2&method=start&path=%5B%22%2Fdownload%2F2013Q1Enhancement%22%5D 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>task id</td><td>String</td><td>A unique ID for the size calculating task.</td><td>2</td></tr></table>

Example: 

```json
{
    "taskid": "51CBD7CD5C76E461"
} 
```

```txt
status 
```

Description: 

Get the status of the size calculating task. 

Availability: 

Since version 2. 

## Request:

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>A unique ID for the task which is obtained from start method.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/FileStation/entry.cgi?api=SYNO.FileStation.DirSize&version=2&method=status&taskid=%2251CBD7CD5C76E461%22 

Response: 

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>finished</td><td>Boolean</td><td>If the task is finished or not.</td><td>2</td></tr><tr><td>num_dir</td><td>Integer</td><td>Number of directories in the queried path(s).</td><td>2</td></tr><tr><td>num_file</td><td>Integer</td><td>Number of files in the queried path(s).</td><td>2</td></tr><tr><td>total_size</td><td>Integer</td><td>Accumulated byte size of the queried path(s).</td><td>2</td></tr></table>

Example: 

```json
{
    "finished": true,
    "num_dir": 3,
    "num_file": 104,
    "total_size": 29973265
} 
```

stop 

Description: 

Stop the calculation. 

## Availability:

Since version 2. 


Request:


<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>tasked</td><td>A unique ID for the task which is obtained from start method.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.DirSize&version=2&method=stop&taskid=%2251CBD7CD5C76E461%22 

## Response:

No specific response. It returns an empty success response if completed without error. 

## API Error Code

No specific API error codes. 

## SYNO.FileStation.MD5

## Description

Get MD5 of a file. 

This is a non-blocking API. You need to start it with the start method. Then, you should poll requests with status method to get the progress status, or make a request with the stop method to cancel the operation. 

## Overview

Availability: Since DSM 6.0 

Version: 2 

## Method

start 

Description: 

Start to get MD5 of a file. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>file_path</td><td>A file path starting with a shared folder for calculating MD5 value.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYN0.FileStation.MD5&version=2&method=start&file_path=%22%2Fdownload%2Fdownload.zip%22 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>task id</td><td>String</td><td>A unique ID for the task for the MD5 calculation task.</td><td>2</td></tr></table>

Example: 

```json
{
    "task id": "51CBD95028B22AED"
} 
```

```txt
status 
```

Description: 

Get the status of the MD5 calculation task. 

Availability: 

Since version 1 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>A unique ID for the task which is obtained from start method.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/FileStation/entry.cgi?api=SYNO.FileStation.MD5&version=2&method=status&task id=%2251CBD95028B22AED%22 

Response: 

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>finished</td><td>Boolean</td><td>Check if the task is finished or not.</td><td>2</td></tr><tr><td>md5</td><td>String</td><td>MD5 of the requested file.</td><td>2</td></tr></table>

Example: 

```json
{
    "finished": true,
    "md5": "6336c5a59aa63dd2042783f88e15410a"
} 
```

stop 

Description: 

Stop calculating the MD5 of a file. 

Availability: 

Since version 23 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>A unique ID for the task which is obtained from start method.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.MD5&version=2&method=stop&taskid=%2251CBD95028B22AED%22 

## Response:

No specific response. It returns an empty success response if completed without error. 

## API Error Code

No specific API error codes. 

# SYNO.FileStation.CheckPermission

## Description

Check if a logged-in user has permission to do file operations on a given folder/file. 

## Overview

Availability: Since DSM 6.0 

Version: 3 

## Method

write 

Description: 

Check if a logged-in user has write permission to create new files/folders in a given folder. 

Availability: 

Since version 3. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>A folder path starting with a shared folder to check write permission.</td><td>String</td><td>(None)</td><td>3 and later</td></tr><tr><td>filename</td><td>A filename you want to write to given path</td><td>String</td><td>(None)</td><td>3 and later</td></tr><tr><td>overwrite</td><td>Optional. The value could be one of following:&quot;true&quot;: overwrite the destination file if one exists.&quot;false&quot;: skip if the destination file exists.Note: when it&#x27;s not specified as true or false, it will be responded with error when the destination file exists.</td><td>Boolean</td><td>(None)</td><td>3 and later</td></tr><tr><td>create_only</td><td>Optional. If set to &quot;true&quot;, the permission will be allowed when there is non-existent file/folder.</td><td>Boolean</td><td>true</td><td>3 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.CheckPermission&version=3&method=write&path=%22%2Fdownload%22&filename=%22test.zip%22&create_only=true 

## Response:

The request will get error response if no write permission for the specified path. 

API Error Code 

No specific API error codes. 

# SYNO.FileStation.Upload

## Description

Upload a file. 

## Overview

Availability: Since DSM 6.0 

Version: 2 

## Method

## upload

Description: 

Upload a file by RFC 1867, http://tools.ietf.org/html/rfc1867. 

Note that each parameter is passed within each part but binary file data must be the last part. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>A destination folder path starting with a shared folder to which files can be uploaded.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>create_parents</td><td>Create parent folder(s) if none exist.</td><td>Boolean</td><td>(None)</td><td>2 and later</td></tr><tr><td>overwrite</td><td>Optional. The value could be one of following:Version 2:true : overwrite the destination file if one exists.false : skip the upload if the destination file exists.Version 3:overwrite: overwrite the destination file if one exists.skip: skip the upload if the destination file exists.Note: when it&#x27;s not specified as true (overwrite) or false (skip), the upload will be responded with error when the destination file exists.</td><td>Version 2:true / false / (None)Version 3:String</td><td>(None)</td><td>2 and later</td></tr><tr><td>mtime</td><td>Optional. Set last modify time of the uploaded file, unit: Linux timestamp in millisecond.</td><td>Linux timestamp in millisecond</td><td>(None)</td><td>2 and later</td></tr><tr><td>crtime</td><td>Optional. Set the create time of the uploaded file, unit: Linux timestamp in millisecond.</td><td>Linux timestamp in millisecond</td><td>(None)</td><td>2 and later</td></tr><tr><td>atime</td><td>Optional. Set last access time of the uploaded file, unit: Linux timestamp in millisecond.</td><td>Linux timestamp in millisecond</td><td>(None)</td><td>2 and later</td></tr><tr><td>filename (file part)</td><td>File content. Must be the last part.</td><td>Binary data</td><td>(None)</td><td>2 and later</td></tr></table>


Note: Linux timestamp in millisecond, defined as the number of milliseconds that have elapsed since 00:00:00 Coordinated Universal Time (UTC), Thursday, 1 January 1970. 


Example: 

POST /webapi/entry.cgi 

... 

Content-Length:20326728 

Content-type: multipart/form-data, boundary=AaB03x 

\- -AaB03x 

content-disposition: form-data; name="api" 

SYNO.FileStation.Upload 

\- -AaB03x 

content-disposition: form-data; name="version" 

2 

\- -AaB03x 

content-disposition: form-data; name="method" 

upload 

\- -AaB03x 

content-disposition: form-data; name="path" 

/upload/test 

\- -AaB03x 

content-disposition: form-data; name="create_parents" 

true 

\- -AaB03x 

content-disposition: form-data; name="file"; filename="file1.txt" 

Content-Type: application/octet-stream 

... contents of file1.txt ... 

\- -AaB03x -- 

Response: 

## No specific response. It returns an empty success response if completed without error.

## API Error Code

<table><tr><td>Code</td><td>Description</td></tr><tr><td>1800</td><td>There is no Content-Length information in the HTTP header or the received size doesn&#x27;t match the value of Content-Length information in the HTTP header.</td></tr><tr><td>1801</td><td>Wait too long, no date can be received from client (Default maximum wait time is 3600 seconds).</td></tr><tr><td>1802</td><td>No filename information in the last part of file content.</td></tr><tr><td>1803</td><td>Upload connection is cancelled.</td></tr><tr><td>1804</td><td>Failed to upload oversized file to FAT file system.</td></tr><tr><td>1805</td><td>Can&#x27;t overwrite or skip the existing file, if no overwrite parameter is given.</td></tr></table>

# SYNO.FileStation.Download

## Description

Download file(s)/folder(s). 

Overview 

Availability: Since DSM 6.0 

Version: 2 

## Method

## download

Description: 

Download files/folders. If only one file is specified, the file content is responded. If more than one file/folder is given, binary content in ZIP format which they are compressed to is responded. 

## Availability:

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>One or more file/folder paths starting with a shared folder to be downloaded, separated by a comma &quot;,&quot; and around the brackets. When more than one file is to be downloaded, files/folders will be compressed as a zip file.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>mode</td><td>Mode used to download files/folders, value could be:&quot;open&quot;: try to trigger the application, such as a web browser, to open it. Content-Type of the HTTP header of the response is set to MIME type according to file extension.&quot;download&quot;: try to trigger the application, such as a web browser, to download it. Content-Type of the HTTP header of response is set to application/octet-stream and Content-Disposition of the HTTP header of the response is set to attachment.</td><td>open or download</td><td>open</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Download&version=2&method=download&path=%5B%22%2Ftest%2FITEMA_20445972-0.mp3%22%5D&mode=%22open%22 

## Response:

The file content. 

## API Error Code

No specific API error codes. 

Note: If mode parameter is set to open value, the status code "404 Not Found" of the HTTP header is responded when an error occurs. 

# SYNO.FileStation.Sharing

## Description

Generate a sharing link to share files/folders with other people and perform operations on sharing link(s). 

## Overview

Availability: Since DSM 6.0 

Version: 3 

## Method

getinfo 

Description: 

Get information of a sharing link by the sharing link ID. 

Availability: 

Since version 3. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>id</td><td>A unique ID of a sharing link.</td><td>String</td><td>(None)</td><td>3 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Sharing&version=3&method=getinfo&id=%22pHTBKQf9%22 

## Response:

Returned <data> object is a <Sharing_Link> object (defined in the Response Objects section). 

Example: 

```json
{
    "date_available": "0",
    "date_expired": "0",
    "has_password": false,
    "id": "pHTBKQf9",
    "isFolder": false,
    "link_owner": "admin",
    "name": "ITEMA_20448251-0.mp3",
    "path": "/test/ITEMA_20448251-0.mp3",
    "status": "valid",
    "url": "http://myds.com:5000/fbsharing/pHTBKQf9"
} 
```

<table><tr><td>list</td></tr></table>

Description: 

List user's file sharing links. 

Availability: 

Since version 1. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>offset</td><td>Optional. Specify how many sharing links are skipped before beginning to return listed sharing links.</td><td>Integer</td><td>0</td><td>3 and later</td></tr><tr><td>limit</td><td>Optional. Number of sharing links requested. 0 means to list all sharing links.</td><td>Integer</td><td>0</td><td>3 and later</td></tr><tr><td>sort_by</td><td>Optional. Specify information of the sharing link to sort on. Options include: id: a unique ID of sharing a file/folder. name: file name. isFolder: if it&#x27;s a folder or not. path: file path. date_expired: the expiration date for the sharing link. date_available: the available date for the sharing link becomes effective. status: the link accessibility status. has_password: If the sharing link is protected or not. url: a URL of a sharing link. link_owner: the user name of the sharing link owner.</td><td>name, isFolder, path, date_expired, date_available, status, has_password, id, url or link_owner</td><td>(None)</td><td>3 and later</td></tr><tr><td>sort_direction</td><td>Optional. Specify to sort ascending or to sort descending. Options include: asc: sort ascending. desc: sort descending.</td><td>asc or desc</td><td>asc</td><td>1 and later</td></tr><tr><td>force_clean</td><td>Optional. If set to false, the data will be retrieved from cache database rapidly. If set to true, all sharing information including sharing statuses and user name of sharing owner will be synchronized. It consumes some time.</td><td>Boolean</td><td>false</td><td>1 and later</td></tr></table>


Example: 


GET /webapi/entry.cgi?api=SYNO.FileStation.Sharing&version=3&method=list&offset=0&limit=10 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>total</td><td>Integer</td><td>Total number of sharing links.</td><td>3</td></tr><tr><td>offset</td><td>Integer</td><td>Requested offset.</td><td>3</td></tr><tr><td>links</td><td></td><td>Array ofobject.</td><td>3</td></tr></table>

## Example:

```json
{
    "links": [
    {
    "date_available": "0",
    "date_expired": "0",
    "has_password": false,
    "id": "pHTBKQf9",
    "isFolder": false,
    "link_owner": "admin",
    "name": "ITEMA_20448251-0.mp3",
    "path": "/test/ITEMA_20448251-0.mp3",
    "status": "valid",
    "url": "http://myds.com:5000/fbsharing/pHTBKQf9"
    }
    ],
    "offset": 0,
    "total": 1
} 
```

## create

Description: 

Generate one or more sharing link(s) by file/folder path(s). 

Availability: 

Since version 3. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>One or more file/folder paths with which to generate sharing links, separated by commas &quot;,&quot;.</td><td>String</td><td>(None)</td><td>3 and later</td></tr><tr><td>password</td><td>Optional The password for the sharing link when accessing it. The max password length are 16 characters.</td><td>String</td><td>(None)</td><td>3 and later</td></tr><tr><td>date_expired</td><td>Optional. The expiration date for the sharing link, written in the format YYYY-MM-DD. When set to 0 (default), the sharing link is permanent.Note: SHOULD put the double quote outside expiration date.</td><td>YYYY-MM-DD</td><td>0</td><td>3 and later</td></tr><tr><td>date_available</td><td>Optional. The available date for the sharing link to become effective, written in the format YYYY-MM-DD. When set to 0 (default), the sharing link is valid immediately after creation.Note: SHOULD put the double quote outside available date.</td><td>YYYY-MM-DD</td><td>0</td><td>3 and later</td></tr></table>


Note: date of date_expired and date_available parameter is based on user's DS date. 


## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Sharing&version=3&method=create&path=%22%2Ftest%2FITEMA_20445972-0.mp3%22&date_expired%222021-12-21%22 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>links</td><td></td><td>Array ofobject.</td><td>3</td></tr></table>

\ object definition: 

<table><tr><td>Member</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>path</td><td>String</td><td>A file/folder path of the sharing link.</td><td>3</td></tr><tr><td>url</td><td>String</td><td>A created URL of the sharing link.</td><td>3</td></tr><tr><td>id</td><td>String</td><td>A created unique ID of the sharing link.</td><td>3</td></tr><tr><td>qrcode</td><td>String</td><td>Base64-encoded image of QR code describing the URL of the sharing link.</td><td>3</td></tr><tr><td>error</td><td>Integer</td><td>0 for creating it successfully, otherwise is the error code for failed to create it.</td><td>3</td></tr></table>

## Example:

```json
{
    "links": [
    {
    "error": 0,
    "id": "y4LmvpaX",
    "path": "/test/ITEMA_20445972-0.mp3",
    "qrcode": "iVBORw0KGgoAAAANSUh...", 
    "url": "http://myds.com:5000/fbsharing/y4LmvpaX"
    }
    ]
} 
```

delete 

Description: 

Delete one or more sharing links. 

Availability: 

Since version 3. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>Id</td><td>Unique IDs of file sharing link(s) to be deleted, separated by commas &quot;,&quot; and around the brackets.</td><td>String</td><td>(None)</td><td>3 and later</td></tr></table>

Example: 

GET /webapi/entry.cgi?api=SYNO.FileStation.Sharing&version=3&method=delete&id=%22y4LmvpaX%22 

Response: 

Returns an empty success response if completed without error, otherwise returns error object array contains failed IDs. 

## clear_invalid

Description: 

Remove all expired and broken sharing links. 

Availability: 

Since version 3. 

Request: 

No parameters are required. 

Example: 

```batch
GET /webapi/entry.cgi?api=SYNO.FileStation.Sharing&version=3&method=clear_invalid 
```

## Response:

No specific response. It returns an empty success response if completed without error. 

edit 

Description: 

Edit sharing link(s). 

Availability: 

Since version 1. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>id</td><td>Unique ID(s) of sharing link(s) to edit, separated by a comma, &quot;,&quot; and around the brackets.</td><td>Integer</td><td>(None)</td><td>3 and later</td></tr><tr><td>password</td><td>Optional. If empty string is set, the password is removed. The max length of the password is 16 characters.</td><td>String</td><td>(None)</td><td>3 and later</td></tr><tr><td>date_expired</td><td>Optional. The expiration date for the sharing link, using format YYYY-MM-DD. When set to 0 (default), the sharing link is permanent.</td><td>YYYY-MM-DD</td><td>(None)</td><td>3 and later</td></tr><tr><td>date_available</td><td>Optional. The available date for the sharing link becomes effective, using format YYYY-MM-DD. When set to 0 (default), the sharing link is valid right after creation.</td><td>YYYY-MM-DD</td><td>(None)</td><td>3 and later</td></tr></table>

Note: date of date_expired and date_available parameter is based on user's DiskStation date. 

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Sharing&version=3&method=edit&id=%22y4LmvpaX%22&password=%22123%22 

## Response:

No specific response. It returns an empty success response if completed without error. 

## Response Objects

<Sharing_Link> object definition: 

<table><tr><td>Member</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>id</td><td>String</td><td>A unique ID of a sharing link.</td><td>3</td></tr><tr><td>url</td><td>String</td><td>A URL of a sharing link.</td><td>3</td></tr><tr><td>link_owner</td><td>String</td><td>A user name of a sharing link owner.</td><td>3</td></tr><tr><td>path</td><td>String</td><td>A file or folder path of a sharing link.</td><td>3</td></tr><tr><td>isFolder</td><td>String</td><td>Whether the sharing link is for a folder.</td><td>3</td></tr><tr><td>has_password</td><td>Boolean</td><td>Whether the sharing link has password.</td><td>3</td></tr><tr><td>date_expired</td><td>String</td><td>The expiration date of the sharing link in the format YYYY-MM-DD. If the value is set to 0, the link will be permanent.</td><td>3</td></tr><tr><td>date_available</td><td>String</td><td>The date when the sharing link becomes active in the format YYYY-MM-DD. If the value is set to 0, the file sharing link will be active immediately after creation.</td><td>3</td></tr><tr><td>status</td><td>String</td><td>The accessibility status of the sharing link might be one of the following:valid: the sharing link is active.invalid: the sharing link is not active because the available date has not arrived yet.expired: the sharing link expired.broken: the sharing link broke due to a change in the file path or access permission.</td><td>3</td></tr></table>

## API Error Code

<table><tr><td>Code</td><td>Description</td></tr><tr><td>2000</td><td>Sharing link does not exist.</td></tr><tr><td>2001</td><td>Cannot generate sharing link because too many sharing links exist.</td></tr><tr><td>2002</td><td>Failed to access sharing links.</td></tr></table>

# SYNO.FileStation.CreateFolder

Description 

Create folders. 

## Overview

Availability: Since DSM 6.0 

Version: 2 

## Method

create 

Description: 

Create folders. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>folder_path</td><td>One or more shared folder paths, separated by commas and around brackets. If force_parent is &quot;true,&quot; and folder_path does not exist, the folder_path will be created. If force_parent is &quot;false,&quot; folder_path must exist or a false value will be returned. The number of paths must be the same as the number of names in the name parameter. The first folder_path parameter corresponds to the first name parameter.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>name</td><td>One or more new folder names, separated by commas &quot;,&quot; and around brackets. The number of names must be the same as the number of folder paths in the folder_path parameter. The first name parameter corresponding to the first folder_path parameter.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>force_parent</td><td>Optional. true: no error occurs if a folder exists and create parent folders as needed. false: parent folders are not created.</td><td>Boolean</td><td>false</td><td>2 and later</td></tr><tr><td>additional</td><td>Optional. Additional requested file information, separated by commas &quot;,&quot; and around brackets. When an additional option is requested, responded objects will be provided in the specified additional option. Options include: real_path: return a real path in volume. size: return file byte size. owner: return information about file owner including user name, group name, UID and GID. time: return information about time including last access time, last modified time, last change time and create time. perm: return information about file permission. type: return a file extension.</td><td>real_path, size, owner, time, perm or type</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.CreateFolder&version=2&method=create&folder_path=%5B%22%2Fvideo%22%5D&name=%5B%22test%22%5D 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>folders</td><td></td><td>Array ofobjects about file information of a new folder path.</td><td>2 and later</td></tr></table>


<file> object definition: 


Same as definition in SYNO.FileStation.List API with getinfo method. 

Example: 

```json
{
    "folders": [
    {
    "isdir": true,
    "name": "test",
    "path": "/video/test"
    }
    ]
} 
```

## API Error Code

<table><tr><td>Code</td><td>Description</td></tr><tr><td>1100</td><td>Failed to create a folder. More information in &lt;errors&gt; object.</td></tr><tr><td>1101</td><td>The number of folders to the parent folder would exceed the system limitation.</td></tr></table>

## SYNO.FileStation.Rename

Description 

Rename a file/folder. 

## Overview

Availability: Since DSM 6.0 

Version: 2 

## Method

rename 

Description: 

Rename a file/folder. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>One or more paths of files/folders to be renamed, separated by commas &quot;,&quot; and around brackets. The number of paths must be the same as the number of names in the name parameter. The first path parameter corresponds to the first name parameter.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>name</td><td>One or more new names, separated by commas &quot;,&quot; and around brackets. The number of names must be the same as the number of folder paths in the path parameter. The first name parameter corresponding to the first path parameter.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>additional</td><td>Optional. Additional requested file information, separated by commas &quot;,&quot; and around brackets. When an additional option is requested, responded objects will be provided in the specified additional option. Options include: real_path: return a real path in volume. size: return file byte size. owner: return information about file owner including user name, group name, UID and GID. time: return information about time including last access time, last modified time, last change time and create time. perm: return information about file permission. type: return a file extension.</td><td>real_path, size, owner, time,perm or type</td><td>(None)</td><td>2 and later</td></tr><tr><td>search_task id</td><td>Optional. A unique ID for the search task which is obtained from start method. It is used to update the renamed file in the search result.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Rename&version=2&method=rename&path=%5B%%222Fvideo%2Ftest%22%5D&name=%5B%22test2%22%5D 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>files</td><td></td><td>Array ofobjects.</td><td>2 and later</td></tr></table>


<file> object definition: 


Same as definition in SYNO.FileStation.List API with getinfo method. 

Example: 

```json
{
    "files": [
    {
    "isdir": true,
    "name": "test2",
    "path": "/video/test2"
    }
    ]
} 
```

## API Error Code

<table><tr><td>Code</td><td>Description</td></tr><tr><td>1200</td><td>Failed to rename it. More information in &lt;errors&gt; object.</td></tr></table>

## SYNO.FileStation.CopyMove

## Description

Copy/move file(s)/folder(s). 

This is a non-blocking API. You need to start to copy/move files with start method. Then, you should poll requests with status method to get the progress status, or make a request with stop method to cancel the operation. 

## Overview

Availability: Since DSM 6.0 

Version: 3 

## Method

start 

Description: 

Start to copy/move files. 

Availability: 

Since version 3. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>One or more copied/moved file/folder path(s) starting with a shared folder, separated by commas &quot;,&quot; and around brackets.</td><td>String</td><td>(None)</td><td>3 and later</td></tr><tr><td>dest_folder_path</td><td>A destination folder path where files/folders are copied/moved.</td><td>String</td><td>(None)</td><td>3 and later</td></tr><tr><td>overwrite</td><td>Optional. true : overwrite all existing files with the same name. false : skip all existing files with the same name. Note: do not overwrite or skip existed files. If there is any existing files, an error occurs (error code: 1003).</td><td>true, false, (None)</td><td>(None)</td><td>3 and later</td></tr><tr><td>remove_src</td><td>Optional. true : move files/folders. false : copy files/folders</td><td>Boolean</td><td>false</td><td>3 and later</td></tr><tr><td>accurate_progress</td><td>Optional. true : calculate the progress by each moved/copied file within sub-folder. false : calculate the progress by files which you give in path parameters. This calculates the progress faster, but is less precise.</td><td>Boolean</td><td>true</td><td>3 and later</td></tr><tr><td>search_task_id</td><td>Optional. A unique ID for the search task which is obtained from SYNO.FileSation.Search API with start method. This is used to update the search result.</td><td>String</td><td>(None)</td><td>3 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.CopyMove&version=3&method=start&path=%5B%22%2Fvideo%2Ftest.avi%22%5D&dest_folder_path=%22%2Fvideo%2Ftest%22 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>task id</td><td>String</td><td>A unique ID for the copy/move task.</td><td>3 and later</td></tr></table>

Example: 

<table><tr><td>{ &quot;taskid&quot;: &quot;FileStation_51D00B7912CDE0B0&quot; }</td></tr></table>

## status

Description: 

Get the copying/moving status. 

Availability: 

Since version 3. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>A unique ID for the copy/move task which is obtained from start method.</td><td>String</td><td>(None)</td><td>3 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.CopyMove&version=3&method=status&taskid=%22FileStation_51D00B7912CDE0B0%22 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>processed_size</td><td>Integer</td><td>If accurate_progress parameter is true, byte sizes of all copied/moved files will be accumulated. If false, only byte sizes of the file you give in path parameter is accumulated.</td><td>3 and later</td></tr><tr><td>total</td><td>Integer</td><td>If accurate_progress parameter is true, the value indicates total byte sizes of files including subfolders will be copied/moved. If false, it indicates total byte sizes of files you give in path parameter excluding files within subfolders. Otherwise, when the total number is calculating, the value is -1.</td><td>3 and later</td></tr><tr><td>path</td><td>String</td><td>A copying/moving path which you give in path parameter.</td><td>3 and later</td></tr><tr><td>finished</td><td>Boolean</td><td>If the copy/move task is finished or not.</td><td>3 and later</td></tr><tr><td>progress</td><td>Double</td><td>A progress value is between 0~1. It is equal to processed_size parameter divided by total parameter.</td><td>3 and later</td></tr><tr><td>dest_folder_path</td><td>String</td><td>A destination folder path where files/folders are copied/moved.</td><td>3 and later</td></tr></table>

## Example:

{ 

"dest_folder_path": "/video/test", 

"finished": false, 

"path": "/video/test.avi", 

"processed_size": 1057, 

"progress": 0.01812258921563625, 

"total": 58325 

<table><tr><td>stop</td></tr></table>

Description: 

Stop a copy/move task. 

Availability: 

Since version 3. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>A unique ID for the copy/move task which is obtained from start method.</td><td>String</td><td>(None)</td><td>3 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.CopyMove&version=1&method=stop&taskid=%22FileStation_51D00B7912CDE0B0%22 

## Response:

No specific response. It returns an empty success response if completed without error. 

## API Error Code

<table><tr><td>Code</td><td>Description</td></tr><tr><td>1000</td><td>Failed to copy files/folders. More information inobject.</td></tr><tr><td>1001</td><td>Failed to move files/folders. More information inobject.</td></tr><tr><td>1002</td><td>An error occurred at the destination. More information inobject.</td></tr><tr><td>1003</td><td>Cannot overwrite or skip the existing file because no overwrite parameter is given.</td></tr><tr><td>1004</td><td>File cannot overwrite a folder with the same name, or folder cannot overwrite a file with the same name.</td></tr><tr><td>1006</td><td>Cannot copy/move file/folder with special characters to a FAT32 file system.</td></tr><tr><td>1007</td><td>Cannot copy/move a file bigger than 4G to a FAT32 file system.</td></tr></table>

## SYNO.FileStation.Delete

## Description

Delete file(s)/folder(s). 

There are two methods; one is a non-blocking method; and the other is a blocking method. With the non-blocking method, you can start the deletion operation using the start method. Then, you should poll a request with the status method to get more information or make a request with the stop method to cancel the operation. With the blocking method, you can directly make requests with delete method to delete files/folders, but the response is not returned until the delete operation is completed. 

## Overview

Availability: Since DSM 6.0 

Version: 2 

## Method

start 

Description: 

Delete file(s)/folder(s). 

This is a non-blocking method. You should poll a request with status method to get more information or make a request with stop method to cancel the operation. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>One or more deleted file/folder paths starting with a shared folder, separated by commas &quot;,&quot; and around brackets.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>accurate_progress</td><td>Optional. true: calculates the progress of each deleted file with the sub-folder recursively. false: calculates the progress of files which you give in path parameters. The latter is faster than recursively, but less precise. Note: Only non-blocking methods suits using the status method to get progress.</td><td>Boolean</td><td>true</td><td>2 and later</td></tr><tr><td>recursive</td><td>Optional. true: Recursively delete files within a folder. false: Only delete first-level file/folder. If a deleted folder contains any file, an error occurs because the folder can&#x27;t be directly deleted.</td><td>Boolean</td><td>true</td><td>2 and later</td></tr><tr><td>search_task id</td><td>Optional. A unique ID for the search task which is obtained from start method. It&#x27;s used to delete the file in the search result.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

```batch
GET /webapi/entry.cgi?api=SYNO.FileStation.Delete&version=2&method=start&path=%22%2Fvideo%2Fdel_folder%22 
```

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>task id</td><td>String</td><td>A unique ID for the delete task.</td><td>2 and later</td></tr></table>

## Example:

```json
{
    "task id": "FileStation_51CEC9C979340E5A"
} 
```

## status

Description: 

Get the deleting status. 

Availability: 

Since version 2. 

## Request:

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>A unique ID for the delete task which is obtained from start method.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Delete&version=2&method=status&taskid=%22FileStation_51CEC9C979340E5A%22 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>processed_num</td><td>Integer</td><td>If accurate_progress parameter is true, the number of all deleted files will be accumulated. If false, only the number of file you give in path parameter is accumulated.</td><td>2 and later</td></tr><tr><td>total</td><td>Integer</td><td>If accurate_progress parameter is true, the value indicates how many files including subfolders will be deleted. If false, it indicates how many files you give in path parameter. When the total number is calculating, the value is -1.</td><td>2 and later</td></tr><tr><td>path</td><td>String</td><td>A deletion path which you give in path parameter.</td><td>2 and later</td></tr><tr><td>processing_path</td><td>String</td><td>A deletion path which could be located at a subfolder.</td><td>2 and later</td></tr><tr><td>finished</td><td>Boolean</td><td>Whether or not the deletion task is finished.</td><td>2 and later</td></tr><tr><td>progress</td><td>Double</td><td>Progress value whose range between 0~1 is equal to processed_num parameter divided by total parameter.</td><td>2 and later</td></tr></table>

## Example:

```json
{
    "finished": false,
    "path": "/video/1000",
    "processed_num": 193,
    "processing_path": "/video/1000/509",
    "progress": 0.03199071809649467,
    "total": 6033
} 
```

## stop

Description: 

Stop a delete task. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>A unique ID for the deletion task which is obtained from start method.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Delete&version=2&method=stop&taskid=%22FileStation_51CEC9C979340E5A%22 

## Response:

No specific response. It returns an empty success response if completed without error. 

delete 

Description: 

Delete files/folders. This is a blocking method. The response is not returned until the deletion operation is completed. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>One or more deleted file/folder path(s) starting with a shared folder, separated by commas &quot;,&quot; and around brackets.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>recursive</td><td>Optional. true : Recursively delete files within a folder. false : Only delete first-level file/folder. If a deleted folder contains any file, an error will occur because the folder can&#x27;t be directly deleted.</td><td>Boolean</td><td>true</td><td>2 and later</td></tr><tr><td>search_task id</td><td>Optional. A unique ID for the search task which is obtained from start method. It&#x27;s used to delete the file in the search result.</td><td>Boolean</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Delete&version=1&method=delete&path=%22%2Fvideo%2Fdel_folder%22 

## Response:

No specific response. It returns an empty success response if completed without error. 

## API Error Code

<table><tr><td>Code</td><td>Description</td></tr><tr><td>900</td><td>Failed to delete file(s)/folder(s). More information inobject.</td></tr></table>

# SYNO.FileStation.Extract

## Description

Extract an archive and perform operations on archive files. 

Note: Supported extensions of archives: zip, gz, tar, tgz, tbz, bz2, rar, 7z, iso. 

## Overview

Availability: Since DSM 6.0 

Version: 2 

## Method

## start

## Description:

Start to extract an archive. This is a non-blocking method. You need to start to extract files with start method. Then, you should poll requests with status method to get the progress status, or make a request with the stop method to cancel the operation. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>file_path</td><td>A file path of an archive to be extracted, starting with a shared folder</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>dest_folder_path</td><td>A destination folder path starting with a shared folder to which the archive will be extracted.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>overwrite</td><td>Optional. Whether or not to overwrite if the extracted file exists in the destination folder.</td><td>Boolean</td><td>false</td><td>2 and later</td></tr><tr><td>keep_dir</td><td>Optional. Whether to keep the folder structure within an archive.</td><td>Boolean</td><td>true</td><td>2 and later</td></tr><tr><td>create_subfolder</td><td>Optional. Whether to create a subfolder with an archive name which archived files are extracted to.</td><td>Boolean</td><td>false</td><td>2 and later</td></tr><tr><td>codepage</td><td>Optional. The language codepage used for decoding file name with an archive.</td><td>DSM supported language, including enu, cht, chs, krn, ger, fre, ita, spn, jpn, dan, nor, sve, nld, rus, plk, ptb, ptg, hun, trk or csv</td><td>DSM Codepage Setting</td><td>2 and later</td></tr><tr><td>password</td><td>Optional. The password for extracting the file.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>item_id</td><td>Optional. Item IDs of archived files used for extracting files within an archive, separated by a comma &quot;,&quot;. Item IDs could be listed by requesting list method.</td><td>Integer</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Extract&version=2&method=start&file_path=%22%2Fdownload%2Fdownload.zip%22&dest_folder_path=%22%2Fdownload%2Fdownload%22&keep_dir=true&create_subfolder=true&overwrite=false 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>task id</td><td>String</td><td>A unique ID for the extract task.</td><td>2</td></tr></table>

Example: 

```json
{
    "taskid": "FileStation_51CBB59C68EFE6A3"
} 
```

## status

Description: 

Get the extract task status. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>A unique ID for the extract task.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Compress&version=2&method=status&taskid=%22FileStation_51CBB59C68EFE6A3%22 

Response: 

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>finished</td><td>Boolean</td><td>If the task is finished or not.</td><td>2</td></tr><tr><td>progress</td><td>Double</td><td>The extract progress expressed in range 0 to 1.</td><td>2</td></tr><tr><td>dest_folder_path</td><td>String</td><td>The requested destination folder for the task.</td><td>2</td></tr></table>

Example: 

```json
{
    "dest_folder_path": "/download/download",
    "finished": false,
    "progress": 0.1
} 
```

stop 

Description: 

Stop the extract task. 

Availability: 

Since version 2. 

## Request:

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>A unique ID for the extract task which is obtained from start method.</td><td>String</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Extract&version=2&method=stop&taskid=%22FileStation_51CBB59C68EFE6A3%22 

## Response:

No specific response. It returns an empty success response if completed without error. 

list 

Description: 

List archived files contained in an archive. 

Availability: 

Since version 2. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>file_path</td><td>An archive file path starting with a shared folder to list.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>offset</td><td>Optional. Specify how many archived files are skipped before beginning to return listed archived files in an archive.</td><td>Integer</td><td>0</td><td>2 and later</td></tr><tr><td>limit</td><td>Optional. Number of archived files requested. -1 indicates to list all archived files in an archive.</td><td>Integer</td><td>-1</td><td>2 and later</td></tr><tr><td>sort_by</td><td>Optional. Specify which archived file information to sort on. Options include: name: file name. size: file size. pack_size: file archived size. mtime: last modified time.</td><td>name, size, pack_size or mtime</td><td>name</td><td>2 and later</td></tr><tr><td>sort_direction</td><td>Optional. Specify to sort ascending or to sort descending. Options include: asc: sort ascending. desc: sort descending.</td><td>asc or desc</td><td>asc</td><td>2 and later</td></tr><tr><td>codepage</td><td>Optional. The language codepage used for decoding file name with an archive.</td><td>DSM supported language, including enu, cht, chs, krn, ger, fre, ita, spn, jpn, dan, nor, sve, nld, rus, plk, ptb, ptg, hun, trk or csy</td><td>DSM Codepage Setting</td><td>2 and later</td></tr><tr><td>password</td><td>Optional. The password for extracting the file.</td><td>String</td><td>(None)</td><td>2 and later</td></tr><tr><td>item_id</td><td>Optional. Item ID of an archived folder to be listed within an archive. (None) or -1 will list archive files in a root folder within an archive.</td><td>Integer</td><td>(None)</td><td>2 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Extract&version=2&method=list&file_path=%22%2Fdownload%2Fdownload.zip%22&sortBy=%22name%22&sort_direction=%22asc%22&item_id=-1 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>items</td><td></td><td>Array ofobjects.</td><td>2</td></tr></table>


<Archive_Item Object> definition: 


<table><tr><td>Member</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>itemid</td><td>Integer</td><td>Item ID of an archived file in an archive.</td><td>2 and later</td></tr><tr><td>name</td><td>String</td><td>Filename of an archived file in an archive.</td><td>2 and later</td></tr><tr><td>size</td><td>Integer</td><td>Original byte size of an archived file.</td><td>2 and later</td></tr><tr><td>pack_size</td><td>Integer</td><td>Archived byte size of an archived file.</td><td>2 and later</td></tr><tr><td>mtime</td><td>String</td><td>Last modified time of an archived file.</td><td>2 and later</td></tr><tr><td>path</td><td>String</td><td>Relative path of an archived file within in an archive.</td><td>2 and later</td></tr><tr><td>is_dir</td><td>Boolean</td><td>Whether an archived file is a folder.</td><td>2 and later</td></tr></table>

## Example:

```json
{
    "items": [
    {
    "is_dir": false,
    "item_id": 1,
    "mtime": "2013-02-03 00:17:12",
    "name": "ITEMA_20445972-0.mp3",
    "pack_size": 51298633,
    "path": "ITEMA_20445972-0.mp3",
    "size": 51726464
    },
    {
    "is_dir": false,
    "item_id": 0,
    "mtime": "2013-03-03 00:18:12",
    "name": "ITEMA_20455319-0.mp3",
    "pack_size": 51434239,
    "path": "ITEMA_20455319-0.mp3",
    "size": 51896448
    }
    ],
    "total": 2
} 
```

## API Error Code


Synology File Station Official API


<table><tr><td>Code</td><td>Description</td></tr><tr><td>1400</td><td>Failed to extract files.</td></tr><tr><td>1401</td><td>Cannot open the file as archive.</td></tr><tr><td>1402</td><td>Failed to read archive data error</td></tr><tr><td>1403</td><td>Wrong password.</td></tr><tr><td>1404</td><td>Failed to get the file and dir list in an archive.</td></tr><tr><td>1405</td><td>Failed to find the item ID in an archive file.</td></tr></table>

# SYNO.FileStation.Compress

## Description

Compress file(s)/folder(s). 

This is a non-blocking API. You need to start to compress files with the start method. Then, you should poll requests with the status method to get compress status, or make a request with the stop method to cancel the operation. 

## Overview

Availability: Since DSM 6.0 

Version: 3 

## Method

start 

Description: 

Start to compress file(s)/folder(s). 

Availability: 

Since version 3. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>path</td><td>One or more file paths to be compressed, separated by commas &quot;,&quot; and around brackets. The path should start with a shared folder.</td><td>String</td><td>(None)</td><td>3 and later</td></tr><tr><td>dest_file_path</td><td>A destination file path (including file name) of an archive for the compressed archive.</td><td>String</td><td>(None)</td><td>3 and later</td></tr><tr><td>level</td><td>Optional. Compress level used, could be one of following values: moderate: moderate compression and normal compression speed. store: pack files with no compress. fastest: fastest compression speed but less compression. best: slowest compression speed but optimal compression.</td><td>moderate, store, fastest or best</td><td>moderate</td><td>3 and later</td></tr><tr><td>mode</td><td>Optional. Compress mode used, could be one of following values: add: Update existing items and add new files. If an archive does not exist, a new one is created. update: Update existing items if newer on the file system and add new files. If the archive does not exist create a new archive. refreshen: Update existing items of an archive if newer on the file system. Does not add new files to the archive. synchronize: Update older files in the archive and add files that are not already in the archive.</td><td>add, update, refreshen or synchronize</td><td>add</td><td>3 and later</td></tr><tr><td>format</td><td>Optional. The compress format, ZIP or 7z format.</td><td>zip or 7z</td><td>zip</td><td>3 and later</td></tr><tr><td>password</td><td>Optional. The password for the archive.</td><td>String</td><td>(None)</td><td>3 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Compress&version=3&method%3Dstart&path=%5B%22%2Fdownload%2FITEMA_20455319-0.mp3%22%2C%22%2Fdownload%2FITEMA_20445972-0.mp3%22%5D&dest_file_path=%22%2Fdownload%2Fdownload.zip%22%26format%3Dzip 

Response: 

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>task id</td><td>String</td><td>A unique ID for the compress task.</td><td>1</td></tr></table>

## Example:

```json
{
    "taskid": "FileStation_51CBB25CC31961FD"
} 
```

## status

Description: 

Get the compress task status. 

Availability: 

Since version 3. 

## Request:

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>A unique ID for the compress task.</td><td>String</td><td>(None)</td><td>3 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Compress&version=3&method=status&taskid=%22FileStation_51CBB25CC31961FD%22 

## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>finished</td><td>Boolean</td><td>Whether or not the compress task is finished.</td><td>3</td></tr><tr><td>dest_file_path</td><td>String</td><td>The requested destination path of an archive.</td><td>3</td></tr></table>

Example: 

```json
{
    "dest_file_path": "/download/download.zip",
    "finished": true
} 
```

## stop

Description: 

Stop the compress task. 

Availability: 

Since version 3. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>A unique ID for the compress task which is obtained from start method.</td><td>String</td><td>(None)</td><td>3 and later</td></tr></table>

## Example:

GET /webapi/entry.cgi?api=SYNO.FileStation.Compress&version=1&method=stop&taskid=%22FileStation_51CBB25CC31961FD%22 

## Response:

No specific response. It returns an empty success response if completed without error. 

## API Error Code

<table><tr><td>Code</td><td>Description</td></tr><tr><td>1300</td><td>Failed to compress files/folders.</td></tr><tr><td>1301</td><td>Cannot create the archive because the given archive name is too long.</td></tr></table>

# SYNO.FileStation.BackgroundTask

## Description

Get information regarding tasks of file operations which is run as the background process including copy, move, delete, compress and extract tasks with non-blocking API/methods. You can use the status method to get more information, or use the stop method to cancel these background tasks in individual API, such as SYNO.FileStation.CopyMove API, SYNO.FileStation.Delete API, SYNO.FileStation.Extract API and SYNO.FileStation.Compress API. 

## Overview

Availability: Since DSM 6.0 

Version: 3 

## Method

## list

Description: 

List all background tasks including copy, move, delete, compress and extract tasks. 

Availability: 

Since version 3. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>offset</td><td>Optional. Specify how many background tasks are skipped before beginning to return listed background tasks.</td><td>Integer</td><td>0</td><td>3 and later</td></tr><tr><td>limit</td><td>Optional. Number of background tasks requested. 0 indicates to list all background tasks.</td><td>Integer</td><td>0</td><td>3 and later</td></tr><tr><td>sort_by</td><td>Optional. Specify which information of the background task to sort on. Options include: crtime: creation time of the background task. finished: Whether the background task is finished.</td><td>crtime or finished</td><td>crtime</td><td>3 and later</td></tr><tr><td>sort_direction</td><td>Optional. Specify to sort ascending or to sort descending. Options include: asc: sort ascending. desc: sort descending.</td><td>asc or desc</td><td>asc</td><td>3 and later</td></tr><tr><td>api_filter</td><td>Optional. List background tasks with one or more given API name(s), separated by commas &quot;,&quot; and around brackets. If not given, all background tasks are listed.</td><td>SYNO.FileStation.CopyMove, SYNO.FileStation.Delete, SYNO.FileStation.Extract or SYNO.FileStation.Compress</td><td>(None)</td><td>3 and later</td></tr></table>

## Example:



GET /webapi/entry.cgi?api=SYNO.FileStation.BackgroundTask&version=3&method=list 



## Response:

<data> object definitions: 

<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>total</td><td>Integer</td><td>Total number of background tasks.</td><td>3 and later</td></tr><tr><td>offset</td><td>Integer</td><td>Requested offset.</td><td>3 and later</td></tr><tr><td>tasks</td><td></td><td>Array ofobjects.</td><td>3 and later</td></tr></table>


<background task> object definition: 


<table><tr><td>Parameter</td><td>Type</td><td>Description</td><td>Availability</td></tr><tr><td>api</td><td>String</td><td>Requested API name.</td><td>3 and later</td></tr><tr><td>version</td><td>String</td><td>Requested API version.</td><td>3 and later</td></tr><tr><td>method</td><td>String</td><td>Requested API method.</td><td>3 and later</td></tr><tr><td>task id</td><td>String</td><td>A requested unique ID for the background task.</td><td>3 and later</td></tr><tr><td>finished</td><td>Boolean</td><td>Whether or not the background task is finished.</td><td>3 and later</td></tr><tr><td>params</td><td>JSON-Style Object</td><td>object. Requested parameters in JSON format according to start method of individual API of the background task.</td><td>3 and later</td></tr><tr><td>path</td><td>String</td><td>A requested path according to start method of individual API of the background task.</td><td>3 and later</td></tr><tr><td>processed_num</td><td>Interger</td><td>A number of processed files according to the response of status method of individual API of the background task.</td><td>3 and later</td></tr><tr><td>processed_size</td><td>Interger</td><td>A processed byte size according to the response of status method of individual API of the background task.</td><td>3 and later</td></tr><tr><td>processing_path</td><td>String</td><td>A processing file path according to the response of status method of individual API of the background task.</td><td>3 and later</td></tr><tr><td>total</td><td>Interger</td><td>A total number/byte size according to the response of status method of individual API of the background task. If API doesn&#x27;t support it, the value is always -1.</td><td>3 and later</td></tr><tr><td>progress</td><td>Double</td><td>A progress value whose range between 0~1 according to the response of status method of individual API of the background task. If API doesn&#x27;t support it, the value is always 0.</td><td>3 and later</td></tr><tr><td>task id</td><td>object</td><td>A unique ID according to the response of start method of individual API of the background task.</td><td>3 and later</td></tr></table>


<params> object definition: 



Requested parameters in JSON format. Please refer to start method in each API. 


Example: 

```json
{
    "tasks": [
    {
    "api": "SYNO.FileStation.CopyMove",
    "crtime": 1372926088,
    "finished": true,
    "method": "start",
    "params": {
    "accurate_progress": true,
    "dest_folder_path": "/video/test",
    "overwrite": true,
    "path": [
    "/video/test2/test.avi"
    ],
    "remove_src": false
    },
    "path": "/video/test2/test.avi",
    "processed_size": 12800,
    "processing_path": "/video/test2/test.avi",
    "progress": 1,
    "taskid": "FileStation_51D53088860DD653",
    "total": 12800,
    "version": 1
    },
    {
    "api": "SYNO.FileStation.Compress",
    "crtime": 1372926097,
    "finished": true,
    "method": "start",
    "params": {
    "dest_file_path": "/video/test/test.zip",
    "format": "zip",
    "level": "",
    "mode": "",
    "password": "",
    "path": [
    "/video/test/test.avi"
    ]
    },
    "progress": 0,
    "taskid": "FileStation_51D53091A82CD948",
    "total": -1,
    "version": 1
    },
    {
    "api": "SYNO.FileStation.Extract",
    "crtime": 1372926103,
    "finished": true,
    "method": "start",
    "params": {
    "create_subfolder": false,
    "dest_folder_path": "/video/test",
    "file_path": [
    "/video/test/test.zip"
    ],
    "keep_dir": true,
    "overwrite": false
    },
    "progress": 1,
    "taskid": "FileStation_51D530978633C014",
    "total": -1,
    "version": 1
    },
    {
    "api": "SYNO.FileStation.Delete", 
```

```json
"crtime": 1372926110,
"finished": true,
"method": "start",
"params": {
    "accurate_progress": true,
    "path": [
    "/video/test/test.avi"
    ]
},
"path": "/video/test/test.avi",
"processed_num": 1,
"processing_path": "/video/test/test.avi",
"progress": 1,
"taskid": "FileStation_51D5309EE1E10BD9",
"total": 1,
"version": 1
}
],
"offset": 0,
"total": 4
} 
```

## clear_finished

Description: 

Delete all finished background tasks. 

Availability: 

Since version 3. 

Request: 

<table><tr><td>Parameter</td><td>Description</td><td>Value</td><td>Default Value</td><td>Availability</td></tr><tr><td>task id</td><td>Unique IDs of finished copy, move, delete, compress or extract tasks. Specify multiple task IDs by &quot;,&quot; and around brackets. If it&#x27;s not given, all finished tasks are deleted.</td><td>String</td><td>(None)</td><td>3 and later</td></tr></table>

## Example:

```batch
GET /webapi/entry.cgi?api=SYNO.FileStation.BackgroundTask&version=3&method=clear_finished 
```

## Response:

No specific response. It returns an empty success response if completed without error. 

## API Error Code

No specific API error codes. 

## Appendix A: Release Notes

Version 2023.03 

- Fix minor bugs 

## Version 2021.03

- Update API for DSM 7.0 release 

- Fix minor bugs 

## Version 2016.03

- Update API for DSM 6.0 release 

## Version 2013.08

- Initial release 