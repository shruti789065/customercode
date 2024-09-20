const { merge } = require('webpack-merge');
const TerserPlugin = require('terser-webpack-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const common = require('./webpack.common.js');

console.log('Loading production configuration');

module.exports = merge(common, {
    mode: 'production',
    optimization: {
        minimize: true,
        minimizer: [
            new TerserPlugin(),
            new CssMinimizerPlugin({
                minimizerOptions: {
                    preset: ['default', {
                        discardComments: {
                            removeAll: true
                        }
                    }],
                }
            }),
        ],
        splitChunks: {
            cacheGroups: {
                main: {
                    chunks: 'all',
                    name: 'site',
                    test: /[\\/]src[\\/]/,
                    enforce: true
                }
            }
        }
    },
    performance: { hints: false }
});

console.log('Production configuration loaded');
