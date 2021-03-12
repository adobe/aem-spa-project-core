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

    const loadScriptsAndStyles = (origin) => {
        return fetch(`${origin}/asset-manifest.json`)
        .then(response => response.json())
        .then(asset => {
            const { entrypoints } = asset;
            if (entrypoints && Array.isArray(entrypoints)) {
                const bodyFragment = document.createDocumentFragment();
                const headFragment = document.createDocumentFragment();

                entrypoints.forEach(item => {
                    const filePath = (`${origin}/_next/${item}`);
                    if(item.indexOf('.css') > 0) {
                        headFragment.appendChild(generateStyleLink(filePath));
                    } else {
                        bodyFragment.appendChild(generateScriptLink(filePath));
                    }
                });
                headFragment.hasChildNodes() && document.head.appendChild(headFragment);
                bodyFragment.hasChildNodes() && document.body.appendChild(bodyFragment);
            }
        });
    };

    const remoteUrl = document.body.dataset.remoteUrl;

    if(remoteUrl) {
        const { origin, pathname } = new URL(remoteUrl);
        fetch(`${origin}/api/getNextProps?path=${pathname}`)
        .then(res => res.json())
        .then(res => {
            document.getElementById("__NEXT_DATA__").textContent = JSON.stringify(res);
        })
        .then(() => loadScriptsAndStyles(origin));
    }
})();
