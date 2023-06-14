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
	'menarinimaster',
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
		// site menarinimaster
		{
			...libsBaseConfig,
			name: 'clientlib-dependencies',
			categories: ['menarinimaster.dependencies'],
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
			categories: ['menarinimaster.site'],
			dependencies: ['menarinimaster.dependencies'],
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

		// site stemline

		{
			...libsBaseConfig,
			name: 'clientlib-dependencies-stemline',
			categories: ['menarinistemline.dependencies'],
			assets: {
				// Copy entrypoint scripts and stylesheets into the respective ClientLib
				// directories
				js: {
					cwd: 'clientlib-dependencies-stemline',
					files: ['**/*.js'],
					flatten: false
				},
				css: {
					cwd: 'clientlib-dependencies-stemline',
					files: ['**/*.css'],
					flatten: false
				}
			}
		},
		{
			...libsBaseConfig,
			name: 'clientlib-stemline',
			categories: ['menarinistemline.site'],
			dependencies: ['menarinistemline.dependencies'],
			assets: {
				// Copy entrypoint scripts and stylesheets into the respective ClientLib
				// directories
				js: {
					cwd: 'clientlib-stemline',
					files: ['**/*.js'],
					flatten: false
				},
				css: {
					cwd: 'clientlib-stemline',
					files: ['**/*.css'],
					flatten: false
				},

				// Copy all other files into the `resources` ClientLib directory
				resources: {
					cwd: 'clientlib-stemline',
					files: ['**/*.*'],
					flatten: false,
					ignore: ['**/*.js', '**/*.css']
				}
			}
		},

		// site Relife-DE

		{
			...libsBaseConfig,
			name: 'clientlib-dependencies-relifede',
			categories: ['menarinirelifede.dependencies'],
			assets: {
				// Copy entrypoint scripts and stylesheets into the respective ClientLib
				// directories
				js: {
					cwd: 'clientlib-dependencies-relifede',
					files: ['**/*.js'],
					flatten: false
				},
				css: {
					cwd: 'clientlib-dependencies-relifede',
					files: ['**/*.css'],
					flatten: false
				}
			}
		},
		{
			...libsBaseConfig,
			name: 'clientlib-relifede',
			categories: ['menarinirelifede.site'],
			dependencies: ['menarinirelifede.dependencies'],
			assets: {
				// Copy entrypoint scripts and stylesheets into the respective ClientLib
				// directories
				js: {
					cwd: 'clientlib-relifede',
					files: ['**/*.js'],
					flatten: false
				},
				css: {
					cwd: 'clientlib-relifede',
					files: ['**/*.css'],
					flatten: false
				},

				// Copy all other files into the `resources` ClientLib directory
				resources: {
					cwd: 'clientlib-relifede',
					files: ['**/*.*'],
					flatten: false,
					ignore: ['**/*.js', '**/*.css']
				}
			}
		},
		// site biotech

		{
			...libsBaseConfig,
			name: 'clientlib-dependencies-biotech',
			categories: ['menarinibiotech.dependencies'],
			assets: {
				// Copy entrypoint scripts and stylesheets into the respective ClientLib
				// directories
				js: {
					cwd: 'clientlib-dependencies-biotech',
					files: ['**/*.js'],
					flatten: false
				},
				css: {
					cwd: 'clientlib-dependencies-biotech',
					files: ['**/*.css'],
					flatten: false
				}
			}
		},
		{
			...libsBaseConfig,
			name: 'clientlib-biotech',
			categories: ['menarinibiotech.site'],
			dependencies: ['menarinibiotech.dependencies'],
			assets: {
				// Copy entrypoint scripts and stylesheets into the respective ClientLib
				// directories
				js: {
					cwd: 'clientlib-biotech',
					files: ['**/*.js'],
					flatten: false
				},
				css: {
					cwd: 'clientlib-biotech',
					files: ['**/*.css'],
					flatten: false
				},

				// Copy all other files into the `resources` ClientLib directory
				resources: {
					cwd: 'clientlib-biotech',
					files: ['**/*.*'],
					flatten: false,
					ignore: ['**/*.js', '**/*.css']
				}
			}
		},
		{
			...libsBaseConfig,
			name: 'clientlib-berlin',
			categories: ['menariniberlin.site'],
			dependencies: ['menariniberlin.dependencies'],
			assets: {
				// Copy entrypoint scripts and stylesheets into the respective ClientLib
				// directories
				js: {
					cwd: 'clientlib-berlin',
					files: ['**/*.js'],
					flatten: false
				},
				css: {
					cwd: 'clientlib-berlin',
					files: ['**/*.css'],
					flatten: false
				},

				// Copy all other files into the `resources` ClientLib directory
				resources: {
					cwd: 'clientlib-berlin',
					files: ['**/*.*'],
					flatten: false,
					ignore: ['**/*.js', '**/*.css']
				}
			}
		}

	]
};
