const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const SOURCE_ROOT = __dirname + '/src/main/webpack';

module.exports = env => {

	const writeToDisk = env && Boolean(env.writeToDisk);

<<<<<<< HEAD
	return merge(common, {
		mode: 'development',
		performance: {
			hints: 'warning',
			maxAssetSize: 1048576,
			maxEntrypointSize: 1048576
		},
		plugins: [
			new HtmlWebpackPlugin({
				inject: true,
				template: path.resolve(__dirname, SOURCE_ROOT + '/menarinimaster/static/index.html'),
				filename: 'menarinimaster.html',
				chunks: ['site']
			}),
			new HtmlWebpackPlugin({
				inject: true,
				template: path.resolve(__dirname, SOURCE_ROOT + '/stemline/static/landing-research.html'),
				filename: 'landing-research.html',
				chunks: ['site', 'stemline']
			}),
			new HtmlWebpackPlugin({
				inject: true,
				template: path.resolve(__dirname, SOURCE_ROOT + '/stemline/static/index.html'),
				filename: 'stemline.html',
				chunks: ['site', 'stemline']
			}),
			new HtmlWebpackPlugin({
				inject: true,
				template: path.resolve(__dirname, SOURCE_ROOT + '/relifede/static/index.html'),
				filename: 'relifede.html',
				chunks: ['site', 'relifede']
			})
		],
		devServer: {
			proxy: [{
				context: ['/content', '/etc.clientlibs'],
				target: 'http://localhost:4502',
			}],
			client: {
				overlay: {
					errors: true,
					warnings: false,
				},
			},
			watchFiles: ['src/**/*'],
			hot: false,
			devMiddleware: {
				writeToDisk: writeToDisk
			}
		}
	});
=======
    return merge(common, {
        mode: 'development',
        performance: {
            hints: 'warning',
            maxAssetSize: 1048576,
            maxEntrypointSize: 1048576
        },
        plugins: [
            new HtmlWebpackPlugin({
                inject: true,
                template: path.resolve(__dirname, SOURCE_ROOT + '/menarinimaster/static/index.html'),
                filename: 'menarinimaster.html',
                chunks: ['site']
            }),
            new HtmlWebpackPlugin({
                inject: true,
                template: path.resolve(__dirname, SOURCE_ROOT + '/stemline/static/landing-research.html'),
                filename: 'landing-research.html',
                chunks: ['site', 'stemline']
           }),
           new HtmlWebpackPlugin({
                inject: true,
                template: path.resolve(__dirname, SOURCE_ROOT + '/stemline/static/pipeline.html'),
                filename: 'pipeline.html',
                chunks: ['site', 'stemline']
           }),
           new HtmlWebpackPlugin({
                inject: true,
                template: path.resolve(__dirname, SOURCE_ROOT + '/stemline/static/index.html'),
                filename: 'stemline.html',
                chunks: ['site', 'stemline']
           })
        ],
        devServer: {
            proxy: [{
                context: ['/content', '/etc.clientlibs'],
                target: 'http://localhost:4502',
            }],
            client: {
                overlay: {
                    errors: true,
                    warnings: false,
                },
            },
            watchFiles: ['src/**/*'],
            hot: false,
            devMiddleware: {
                writeToDisk: writeToDisk
            }
        }
    });
>>>>>>> Stemline
};
