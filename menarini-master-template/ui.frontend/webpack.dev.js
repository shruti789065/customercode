const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const SOURCE_ROOT = __dirname + '/src/main/webpack';

module.exports = env => {

	const writeToDisk = env && Boolean(env.writeToDisk);

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
				template: path.resolve(__dirname, SOURCE_ROOT + '/menarinimaster/static/efpia.html'),
				filename: 'efpia.html',
				chunks: ['site']
			}),
			new HtmlWebpackPlugin({
				inject: true,
				template: path.resolve(__dirname, SOURCE_ROOT + '/menarinimaster/static/library.html'),
				filename: 'library.html',
				chunks: ['site']
			}),
			new HtmlWebpackPlugin({
				inject: true,
				template: path.resolve(__dirname, SOURCE_ROOT + '/menarinimaster/static/content-page.html'),
				filename: 'content-page.html',
				chunks: ['site']
			}),
			new HtmlWebpackPlugin({
				inject: true,
				template: path.resolve(__dirname, SOURCE_ROOT + '/menarinimaster/static/internal-page.html'),
				filename: 'internal-page.html',
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
				template: path.resolve(__dirname, SOURCE_ROOT + '/stemline/static/products.html'),
				filename: 'products.html',
				chunks: ['site', 'stemline']
			}),
			new HtmlWebpackPlugin({
				inject: true,
				template: path.resolve(__dirname, SOURCE_ROOT + '/stemline/static/clinical-trials.html'),
				filename: 'clinical-trials.html',
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
				template: path.resolve(__dirname, SOURCE_ROOT + '/biotech/static/index.html'),
				filename: 'biotech.html',
				chunks: ['site', 'biotech']
			}),
			new HtmlWebpackPlugin({
				inject: true,
				template: path.resolve(__dirname, SOURCE_ROOT + '/berlin/static/index.html'),
				filename: 'berlin.html',
				chunks: ['site', 'berlin']
			}),
			new HtmlWebpackPlugin({
				inject: true,
				template: path.resolve(__dirname, SOURCE_ROOT + '/berlinchemie/static/index.html'),
				filename: 'berlinchemie.html',
				chunks: ['site', 'berlinchemie']
			}),
			new HtmlWebpackPlugin({
				inject: true,
				template: path.resolve(__dirname, SOURCE_ROOT + '/apac/static/index.html'),
				filename: 'apac.html',
				chunks: ['site', 'apac']
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
};
