"use strict";

// @todo: Check if it can be rewritten in ES6 module syntax.
const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const TSConfigPathsPlugin = require("tsconfig-paths-webpack-plugin");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const ESLintPlugin = require("eslint-webpack-plugin");
const glob = require("glob");

const SOURCE_ROOT = path.resolve(__dirname, "src/main/webpack");

const componentEntries = glob.sync(`${SOURCE_ROOT}/components/**/`, {
  ignore: [`${SOURCE_ROOT}/components/site/**`],
}).reduce((entries, componentDir) => {
  const folderName = path.basename(componentDir);

  const jsFile = path.join(componentDir, `${folderName}.js`);
  const scssFile = path.join(componentDir, `${folderName}.scss`);

  // Check if both JS and SCSS files exist for the component
  const entryFiles = [];
  if (glob.sync(jsFile).length) entryFiles.push(jsFile);
  if (glob.sync(scssFile).length) entryFiles.push(scssFile);

  if (entryFiles.length) {
    entries[folderName] = entryFiles; // Add entries only if files are present
  }

  return entries;
}, {});

// Add `site` entry manually for global assets
const entries = {
  site: path.join(SOURCE_ROOT, "site/main.ts"),
  ...componentEntries,
};

const resolve = {
  extensions: [".js", ".ts"],
  plugins: [
    new TSConfigPathsPlugin({
      configFile: "./tsconfig.json",
    }),
  ],
};

module.exports = {
  resolve,
  entry: entries,
  output: {
    filename: (chunkData) => {
      return chunkData.chunk.name === "dependencies"
        ? "clientlib-dependencies/[name].js"
        : "clientlib-[name]/[name].js";
    },
    path: path.resolve(__dirname, "dist"),
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        exclude: /node_modules/,
        use: [
          {
            loader: "ts-loader",
          },
          {
            loader: "glob-import-loader",
            options: {
              resolve,
            },
          },
        ],
      },
      {
        test: /\.scss$/,
        use: [
          MiniCssExtractPlugin.loader,
          {
            loader: "css-loader",
            options: {
              url: false,
            },
          },
          {
            loader: "sass-loader",
          },
          {
            loader: "glob-import-loader",
            options: {
              resolve,
            },
          },
        ],
      },
      {
        test: /\.css$/,
        use: [
          MiniCssExtractPlugin.loader,
          {
            loader: "css-loader",
            options: {
              url: false,
            },
          },
        ],
      },
    ],
  },
  plugins: [
    new CleanWebpackPlugin(),
    new ESLintPlugin({
      extensions: ["js", "ts", "tsx"],
    }),
    new MiniCssExtractPlugin({
      // Output CSS files for each component and the main site
      filename: (chunkData) => {
        return chunkData.chunk.name === "site"
          ? "clientlib-site/[name].css"
          : `clientlib-[name]/[name].css`;
      },
    }),
    new CopyWebpackPlugin({
      patterns: [
        {
          from: path.resolve(SOURCE_ROOT, "resources"),
          to: "clientlib-site/",
        },
      ],
    }),
  ],
  stats: {
    assetsSort: "chunks",
    builtAt: true,
    children: false,
    chunkGroups: true,
    chunkOrigins: true,
    colors: false,
    errors: true,
    errorDetails: true,
    env: true,
    modules: false,
    performance: true,
    providedExports: false,
    source: false,
    warnings: true,
  },
};
