<!--
* Copyright 2020 Adobe. All rights reserved.
* This file is licensed to you under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License. You may obtain a copy
* of the License at http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software distributed under
* the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
* OF ANY KIND, either express or implied. See the License for the specific language
* governing permissions and limitations under the License.
-->
RemotePageNext
====
Custom page component for editing remote Next.js SPA within AEM. This component fetches all the necessary assets from the application's generated asset-manifest.json and uses this for rendering the SPA within AEM.


## Requirements

### Create and expose an asset-manifest.json file
In a React app, running `npm run build` generates an `asset-manifest.json` file using the [webpack-manifest-plugin](https://www.npmjs.com/package/webpack-manifest-plugin) which will contain the paths of all generated assets.
Using a similar approach, we need to add the necessary configs in the Next.js app to output a similar asset-manifest.json file. This file can then be used by AEM  to fetch the required scripts or styles for rendering the remote application.
Here we are using the [next-assets-manifest](https://www.npmjs.com/package/next-assets-manifest) package to generate the asset-manifest file in the Next.js app and transforming it to have a similar structure as the React version.

- `npm install next.config.js`
- If not available already, create a new file `next.config.js` in the project root.
- In this, add the following snippet.

```javascript
const withAssetsManifest = require('next-assets-manifest');

module.exports = withAssetsManifest({
  assetsManifest: {
    output: "../public/asset-manifest.json",
    transform: (assets, manifest) => {
        const entrypoints = [];
      	for(let file in assets) {
            if(assets[file].endsWith(".js") || assets[file].endsWith(".css")) {
            	entrypoints.push(assets[file]);
            }
        }
        return {
          files: assets,
          entrypoints: entrypoints
        };
      }
  }
});
```

### Create an endpoint to fetch the app's initial data
A Next.js app does initial data population for a page using data props set in the page's DOM. For remotely loading the app inside AEM, we need to fetch this data and set it in the AEM RemotePageNext component.
- ```npm install xmldom ```
- Create `/pages/api/getNextProps.js`
- Add the following snippet in it:

```javascript
const { DOMParser } = require('xmldom');
const { NEXT_PUBLIC_URL} = process.env;

export default function handler(req, res) {
  let { path } = req.query;

  fetch(NEXT_PUBLIC_URL + (path || ''))
    .then(t => t.text())
    .then(t => {
        const parser = new DOMParser();
	    const doc = parser.parseFromString(t, 'text/html');
        const data = doc.getElementById("__NEXT_DATA__").textContent;
        res.status(200).json(data);
    });
}
```
where `NEXT_PUBLIC_URL` is the origin of the Next.js app to be edited.
_Note: Please ensure CORS configuration as detailed in the next step has been done to allow the AEM instance to be able to fetch this data._

### Enable CORS in Developement
Since AEM needs to fetch the data props of the Next.js app hosted on a different domain, we need to enable CORS in the application.This can be done adding the necessary response header using the `next.config.js` file

A simple implementation is as follows-
- Add the following content in the `next.config.js` file created during the [asset-manifest step](#create-and-expose-an-asset-manifest.json-file)

```javascript
const { NEXT_PUBLIC_AEM_HOST_URI } = process.env;

module.exports = withAssetsManifest({
    async headers() {
      return [
          {
            source: '/api/getNextProps',
            headers: [
              {
                key: 'Access-Control-Allow-Origin',
                value: NEXT_PUBLIC_AEM_HOST_URI
              },
            ],
          },
        ]
    }
    ...
})
```
where `NEXT_PUBLIC_AEM_HOST_URI` is the origin of the AEM instance in which the remote SPA will be edited(_eg: http://localhost:4502_). To allow all origins, you can give "*" as the value as well.


### Configure remote URL
The remote application's URL can be set via the Page Properties, which is then written to JCR for this RemotePageNext component. The property defined for this is `./remoteSPAUrl`
The URL to be provided is the URL of the specific Next.js page to be edited. In most cases, this would be the host URL of the application.
For eg:  for editing a remote Next.js page at `https://test.com/abc`, this same URL needs to be provided in AEM as well.


## Limitations
-
