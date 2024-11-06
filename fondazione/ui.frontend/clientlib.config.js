/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe Systems Incorporated
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

const path = require('path');

const BUILD_DIR = path.join(__dirname, 'dist');
const CLIENTLIB_DIR = path.join(
  __dirname,
  '..',
  'ui.apps',
  'src',
  'main',
  'content',
  'jcr_root',
  'apps',
  'fondazione',
  'clientlibs'
);

const libsBaseConfig = {
  allowProxy: true,
  serializationFormat: 'xml',
  cssProcessor: ['default:none', 'min:none'],
  jsProcessor: ['default:none', 'min:none']
};

// Config for `aem-clientlib-generator`
module.exports = {
  context: BUILD_DIR,
  clientLibRoot: CLIENTLIB_DIR,
  libs: [
    {
      ...libsBaseConfig,
      name: 'clientlib-dependencies',
      categories: ['fondazione.dependencies'],
      assets: {
        // Copy entrypoint scripts and stylesheets into the respective ClientLib
        // directories
        js: {
          cwd: 'clientlib-dependencies',
          files: ['**/*.js'],
          flatten: false
        },
        css: {
          cwd: 'clientlib-dependencies',
          files: ['**/*.css'],
          flatten: false
        }
      }
    },
    {
      ...libsBaseConfig,
      name: 'clientlib-site',
      categories: ['fondazione.site'],
      dependencies: ['fondazione.dependencies'],
      assets: {
        // Copy entrypoint scripts and stylesheets into the respective ClientLib
        // directories
        js: {
          cwd: 'clientlib-site',
          files: ['**/*.js'],
          flatten: false
        },
        css: {
          cwd: 'clientlib-site',
          files: ['**/*.css'],
          flatten: false
        },

        // Copy all other files into the `resources` ClientLib directory
        resources: {
          cwd: 'clientlib-site',
          files: ['**/*.*'],
          flatten: false,
          ignore: ['**/*.js', '**/*.css']
        }
      }
    },
    {
      ...libsBaseConfig,
      name: 'clientlib-signin',
      categories: ['fondazione.signin'],
      dependencies: ['fondazione.dependencies'],
      assets: {
        // Copy entrypoint scripts and stylesheets into the respective ClientLib
        // directories
        js: {
          cwd: 'clientlib-signin',
          files: ['**/*.js'],
          flatten: false
        },
        css: {
          cwd: 'clientlib-signin',
          files: ['**/*.css'],
          flatten: false
        },

        // Copy all other files into the `resources` ClientLib directory
        resources: {
          cwd: 'clientlib-signin',
          files: ['**/*.*'],
          flatten: false,
          ignore: ['**/*.js', '**/*.css']
        }
      }
    },
    {
      ...libsBaseConfig,
      name: 'clientlib-signup',
      categories: ['fondazione.signup'],
      dependencies: ['fondazione.dependencies'],
      assets: {
        // Copy entrypoint scripts and stylesheets into the respective ClientLib
        // directories
        js: {
          cwd: 'clientlib-signup',
          files: ['**/*.js'],
          flatten: false
        },
        css: {
          cwd: 'clientlib-signup',
          files: ['**/*.css'],
          flatten: false
        },

        // Copy all other files into the `resources` ClientLib directory
        resources: {
          cwd: 'clientlib-signup',
          files: ['**/*.*'],
          flatten: false,
          ignore: ['**/*.js', '**/*.css']
        }
      }
    },
    {
      ...libsBaseConfig,
      name: 'clientlib-editprofile',
      categories: ['fondazione.editprofile'],
      dependencies: ['fondazione.dependencies'],
      assets: {
        // Copy entrypoint scripts and stylesheets into the respective ClientLib
        // directories
        js: {
          cwd: 'clientlib-editprofile',
          files: ['**/*.js'],
          flatten: false
        },
        css: {
          cwd: 'clientlib-editprofile',
          files: ['**/*.css'],
          flatten: false
        },

        // Copy all other files into the `resources` ClientLib directory
        resources: {
          cwd: 'clientlib-editprofile',
          files: ['**/*.*'],
          flatten: false,
          ignore: ['**/*.js', '**/*.css']
        }
      }
    },
    {
      ...libsBaseConfig,
      name: 'clientlib-editimage',
      categories: ['fondazione.editimage'],
      dependencies: ['fondazione.dependencies'],
      assets: {
        // Copy entrypoint scripts and stylesheets into the respective ClientLib
        // directories
        js: {
          cwd: 'clientlib-editimage',
          files: ['**/*.js'],
          flatten: false
        },
        css: {
          cwd: 'clientlib-editimage',
          files: ['**/*.css'],
          flatten: false
        },

        // Copy all other files into the `resources` ClientLib directory
        resources: {
          cwd: 'clientlib-editimage',
          files: ['**/*.*'],
          flatten: false,
          ignore: ['**/*.js', '**/*.css']
        }
      }
    }
  ]
};
