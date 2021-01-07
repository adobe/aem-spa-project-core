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
RemotePage
====
Custom page component for editing remote React SPA within AEM. This component fetches all the necessary assets from the application's generated asset-manifest.json and uses this for rendering the SPA within AEM.

## Asset manifest and why is it used here?
When `npm run build` is run in a create-react-app project, react-scripts uses the [webpack-manifest-plugin](https://www.npmjs.com/package/webpack-manifest-plugin) to output an `asset-manifest.json` file which will contain the paths of all generated assets. This file can be used by AEM  to fetch the required scripts or styles for rendering the remote application.
- Please note that running `npm start` will not generate an asset-manifest as the files are compiled in-memory and nothing is written.
- By default, CRA will assume your application is hosted at the serving web server's root or a subpath as specified in package.json (homepage) and will hence ignore the hostname. This means that all the paths will be relative in the asset-manifest.json. If the assets need to be referenced verbatim to the url you provide (hostname included), you can use the `PUBLIC_URL` environment variable [as recommended by React](https://create-react-app.dev/docs/advanced-configuration/)


## Remote URL configuration
The remote application's URL can be set via the Page Properties, which is then written to JCR for this RemotePage component. The property defined for this is `./remoteSPAUrl`
The URL to be provided is the location at which the asset-manifest exists. In most cases, this would be the host URL of the application.
For eg:  for a remote react application at `https://test.com` with the generated asset manifest at `https://test.com/asset-manifest.json` the URL to be provided would be `https://test.com`.

## Rendering remote SPA in AEM
By default, the remotepage component creates an element -

`<div id="root"></div>`

 which will behave as the root DOM node in which the react application is rendered.
If the remote application to be edited has a different root DOM element/id, please override the [body.html](./body.html) in the extended component.

## Restrictions
- Current implementation support remote React applications only.
- Internal css defined in the application's root html file as well as inline css on the root DOM node will not be available when doing remote rendering in AEM. However all external style sheets will be available as expected, as well as all styles within the React application.
