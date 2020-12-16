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


(function (document) {

  const generateScriptLink = (path) => {
    const element = document.createElement("script");
    element.type = "text/javascript"
    element.src = path;
    element.crossOrigin = '';

    return element;
  }
  const domain = document.body.dataset.remoteUrl;
  if (domain) {
      console.log(`Fetching asset manifest from ${domain}`);
    fetch(domain + '/asset-manifest.json')
      .then(response => response.json())
      .then(asset => {
        console.log(`Got asset entries`, asset);
        const entrypoints = asset.entrypoints;

        entrypoints.client.js.forEach ( entry => {
            const filePath = `${domain}${entry}`;
            console.log(`Generate script tag for ${filePath}`);
            document.body.appendChild(generateScriptLink(filePath));
        })
      });
  }
})(document);
