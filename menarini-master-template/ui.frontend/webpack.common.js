/* eslint-disable max-len */
/* eslint-disable @typescript-eslint/no-var-requires */
"use strict";

const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const TSConfigPathsPlugin = require("tsconfig-paths-webpack-plugin");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const ESLintPlugin = require("eslint-webpack-plugin");

const SOURCE_ROOT = __dirname + "/src/main/webpack";
const SITE_masterTemplate = "/mastertemplate";
const SITE_menarinimaster = "/menarinimaster";
const SITE_stemline = "/stemline";
const SITE_relifede = "/relifede";
const SITE_biotech = "/biotech";
const SITE_berlin = "/berlin";
const SITE_berlinchemie = "/berlinchemie";
const SITE_apac = "/apac";

const resolve = {
  extensions: [".js", ".ts"],
  plugins: [
    new TSConfigPathsPlugin({
      configFile: "./tsconfig.json",
    }),
  ],
};

module.exports = {
  resolve: resolve,
  entry: {
    site: SOURCE_ROOT + SITE_menarinimaster + "/site/main.ts",
    stemline: SOURCE_ROOT + SITE_stemline + "/site/main.ts",
    relifede: SOURCE_ROOT + SITE_relifede + "/site/main.ts",
    biotech: SOURCE_ROOT + SITE_biotech + "/site/main.ts",
    berlin: SOURCE_ROOT + SITE_berlin + "/site/main.ts",
    berlinchemie: SOURCE_ROOT + SITE_berlinchemie + "/site/main.ts",
    apac: SOURCE_ROOT + SITE_apac + "/site/main.ts",
  },
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
              resolve: resolve,
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
            loader: "postcss-loader",
            options: {
              postcssOptions: {
                plugins: [require("autoprefixer")],
              },
            },
          },
          {
            loader: "sass-loader",
          },
          {
            loader: "glob-import-loader",
            options: {
              resolve: resolve,
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
      filename: "clientlib-[name]/[name].css",
    }),
    new CopyWebpackPlugin({
      patterns: [
        {
          from: path.resolve(
            __dirname,
            SOURCE_ROOT + SITE_masterTemplate + "/resources/"
          ),
          to: "./clientlib-site",
        },
        {
          from: path.resolve(
            __dirname,
            SOURCE_ROOT + SITE_menarinimaster + "/resources/"
          ),
          to: "./clientlib-site",
        },
        {
          from: path.resolve(
            __dirname,
            SOURCE_ROOT + SITE_stemline + "/resources/"
          ),
          to: "./clientlib-stemline",
        },
        {
          from: path.resolve(
            __dirname,
            SOURCE_ROOT + SITE_relifede + "/resources/"
          ),
          to: "./clientlib-relifede",
        },
        {
          from: path.resolve(
            __dirname,
            SOURCE_ROOT + SITE_biotech + "/resources/"
          ),
          to: "./clientlib-biotech",
        },
        {
          from: path.resolve(
            __dirname,
            SOURCE_ROOT + SITE_berlin + "/resources/"
          ),
          to: "./clientlib-berlin",
        },
        {
          from: path.resolve(
            __dirname,
            SOURCE_ROOT + SITE_berlinchemie + "/resources/"
          ),
          to: "./clientlib-berlinchemie",
        },
        {
          from: path.resolve(
            __dirname,
            SOURCE_ROOT + SITE_apac + "/resources/"
          ),
          to: "./clientlib-apac",
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
