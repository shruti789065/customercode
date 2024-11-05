"use strict";

// @todo: Check if it can be rewritten in ES6 module syntax.
const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const TSConfigPathsPlugin = require("tsconfig-paths-webpack-plugin");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const ESLintPlugin = require("eslint-webpack-plugin");

// Internal scripts.
const componentNames = require('./src/main/webpack/config/componentNames');

const SOURCE_ROOT = path.resolve(__dirname, "src/main/webpack");

// Generate SCSS entry points for each component
const componentScssEntries = componentNames.reduce((entries, name) => {
  entries[name] = path.join(SOURCE_ROOT, `components/${name}/${name}.scss`);
  return entries;
}, {});

// Generate patterns for copying JS.
const componentPatterns = componentNames.map((name) => {
  return {
    from: path.resolve(SOURCE_ROOT, `components/${name}/${name}.js`),
    to: `clientlib-${name}/`,
  };
});

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
  entry: {
    site: path.join(SOURCE_ROOT, "site/main.ts"),
    ...componentScssEntries, // Add component SCSS entries
  },
  output: {
    filename: (chunkData) => {
      return chunkData.chunk.name === "dependencies"
        ? "clientlib-dependencies/[name].js"
        : "clientlib-site/[name].js";
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
        ...componentPatterns,
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
