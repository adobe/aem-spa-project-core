/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

 (function(){

    const generateScriptLink = (path) => {
        const element = document.createElement('script');
        element.type = 'text/javascript'
        element.src = path;

        return element;
    };

    const generateStyleLink = (path) => {
        const element = document.createElement('link');
        element.type = 'text/css';
        element.rel = 'stylesheet';
        element.href = path;

        return element;
    };

    const sanitizeUrl = (url) => {
        let { pathname, origin } = new URL(url);
        pathname.replace(/\/\//, '/');
        return `${origin}${pathname}`;
    };

     const domain = document.body.dataset.remoteUrl;
     if(domain) {
         const manifestUrl = sanitizeUrl(`${domain}/asset-manifest.json`);
         fetch(manifestUrl)
           .then(response => response.json())
           .then(asset => {
              const { entrypoints } = asset;
              const files = Array.isArray(entrypoints) ? entrypoints : entrypoints.client.js;
              files.forEach(item => {
                const filePath = sanitizeUrl(`${domain}/${item}`);
                let element;
         		if(item.indexOf('.css') > 0) {
                    element = generateStyleLink(filePath);
                    document.head.appendChild(element);
         		} else {
                    element = generateScriptLink(filePath);
                    document.body.appendChild(element);
                 }
             });
         });
     }
 })();
