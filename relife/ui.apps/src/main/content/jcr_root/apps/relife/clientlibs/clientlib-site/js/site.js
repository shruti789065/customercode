/*
 * ATTENTION: The "eval" devtool has been used (maybe by default in mode: "development").
 * This devtool is neither made for production nor for readable output files.
 * It uses "eval()" calls to create a separate source file in the browser devtools.
 * If you are trying to read the output file, select a different devtool (https://webpack.js.org/configuration/devtool/)
 * or disable the default devtool with "devtool: false".
 * If you are looking for production-ready output files, see mode: "production" (https://webpack.js.org/configuration/mode/).
 */
/******/ (function() { // webpackBootstrap
/******/ 	var __webpack_modules__ = ({

/***/ "./src/main/webpack/site/main.scss":
/*!*****************************************!*\
  !*** ./src/main/webpack/site/main.scss ***!
  \*****************************************/
/***/ (function(__unused_webpack_module, __webpack_exports__, __webpack_require__) {

"use strict";
eval("__webpack_require__.r(__webpack_exports__);\n// extracted by mini-css-extract-plugin\n\n\n//# sourceURL=webpack://aem-maven-archetype/./src/main/webpack/site/main.scss?");

/***/ }),

/***/ "./src/main/webpack/site/main.ts":
/*!***************************************!*\
  !*** ./src/main/webpack/site/main.ts ***!
  \***************************************/
/***/ (function(__unused_webpack_module, __webpack_exports__, __webpack_require__) {

"use strict";
eval("__webpack_require__.r(__webpack_exports__);\n/* harmony import */ var _main_scss__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./main.scss */ \"./src/main/webpack/site/main.scss\");\n/* harmony import */ var C_Aem_Cloud_Repo_git_Menarini_Official_relife_ui_frontend_src_main_webpack_site_util_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./src/main/webpack/site/_util.js */ \"./src/main/webpack/site/_util.js\");\n/* harmony import */ var C_Aem_Cloud_Repo_git_Menarini_Official_relife_ui_frontend_src_main_webpack_site_Header_header_js__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./src/main/webpack/site/Header/header.js */ \"./src/main/webpack/site/Header/header.js\");\n/* harmony import */ var C_Aem_Cloud_Repo_git_Menarini_Official_relife_ui_frontend_src_main_webpack_site_Header_header_js__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(C_Aem_Cloud_Repo_git_Menarini_Official_relife_ui_frontend_src_main_webpack_site_Header_header_js__WEBPACK_IMPORTED_MODULE_2__);\n/* harmony import */ var C_Aem_Cloud_Repo_git_Menarini_Official_relife_ui_frontend_src_main_webpack_site_main_ts__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./src/main/webpack/site/main.ts */ \"./src/main/webpack/site/main.ts\");\n\r\n\r\n\r\n\r\n;\r\n\n\n//# sourceURL=webpack://aem-maven-archetype/./src/main/webpack/site/main.ts?");

/***/ }),

/***/ "./src/main/webpack/site/Header/header.js":
/*!************************************************!*\
  !*** ./src/main/webpack/site/Header/header.js ***!
  \************************************************/
/***/ (function() {

eval("\n\n//# sourceURL=webpack://aem-maven-archetype/./src/main/webpack/site/Header/header.js?");

/***/ }),

/***/ "./src/main/webpack/site/_util.js":
/*!****************************************!*\
  !*** ./src/main/webpack/site/_util.js ***!
  \****************************************/
/***/ (function(__unused_webpack_module, __webpack_exports__, __webpack_require__) {

"use strict";
eval("__webpack_require__.r(__webpack_exports__);\n/* harmony export */ __webpack_require__.d(__webpack_exports__, {\n/* harmony export */   _findSiblingsWithClass: function() { return /* binding */ _findSiblingsWithClass; },\n/* harmony export */   _generateUniqueValue: function() { return /* binding */ _generateUniqueValue; },\n/* harmony export */   _getJsonProperty: function() { return /* binding */ _getJsonProperty; },\n/* harmony export */   _isDesktop: function() { return /* binding */ _isDesktop; },\n/* harmony export */   _prependHtml: function() { return /* binding */ _prependHtml; },\n/* harmony export */   getUrl: function() { return /* binding */ getUrl; },\n/* harmony export */   hideOverlayAndLoader: function() { return /* binding */ hideOverlayAndLoader; },\n/* harmony export */   showOverlayAndLoader: function() { return /* binding */ showOverlayAndLoader; }\n/* harmony export */ });\nfunction _isDesktop() {\r\n  if (window.innerWidth < 1200) {\r\n    return false;\r\n  } else {\r\n    return true;\r\n  }\r\n}\r\n\r\nfunction _prependHtml(el, str) {\r\n  var div = document.createElement(\"div\");\r\n  div.innerHTML = str;\r\n  while (div.children.length > 0) {\r\n    el.prepend(div.children[0]);\r\n  }\r\n}\r\n\r\nfunction _findSiblingsWithClass(element, className) {\r\n  const siblings = [];\r\n  let sibling = element.parentNode.firstChild;\r\n  while (sibling) {\r\n    if (\r\n      sibling.nodeType === 1 &&\r\n      sibling !== element &&\r\n      sibling.classList.contains(className)\r\n    ) {\r\n      siblings.push(sibling);\r\n    }\r\n    sibling = sibling.nextSibling;\r\n  }\r\n  return siblings;\r\n}\r\n\r\nfunction _getJsonProperty(jsonData, parameter) {\r\n  const items = [];\r\n  for (const key in jsonData) {\r\n    if (jsonData.hasOwnProperty(key)) {\r\n      const fragment = jsonData[key];\r\n      if (fragment.hasOwnProperty(parameter)) {\r\n        items.push(fragment[parameter]);\r\n      }\r\n    }\r\n  }\r\n\r\n  return items;\r\n}\r\n\r\nfunction _generateUniqueValue(name, key) {\r\n  // Rimuovi spazi e converti il nome e la chiave in minuscolo\r\n  const cleanName = name.toLowerCase().replace(/\\s+/g, \"-\");\r\n  const cleanKey = key.toLowerCase().replace(/\\s+/g, \"-\");\r\n\r\n  // Estrai le prime tre lettere dal nome e dalla chiave\r\n  const shortName = cleanName.substring(0, 4);\r\n  const shortKey = cleanKey.substring(0, 4);\r\n\r\n  // Combina le parti per creare il valore univoco\r\n  const uniqueValue = `${shortName}-${shortKey}`;\r\n\r\n  return uniqueValue;\r\n}\r\n\r\nfunction showOverlayAndLoader(item, needOverlay) {\r\n  if (needOverlay) {\r\n    const overlay = document.createElement(\"div\");\r\n    overlay.classList.add(\"overlay\");\r\n    item.append(overlay);\r\n  }\r\n  const loader = document.createElement(\"div\");\r\n  loader.classList.add(\"loader\");\r\n  item.append(loader);\r\n  item.addClass(\"loading\");\r\n}\r\n\r\nfunction hideOverlayAndLoader(item) {\r\n  item.find(\".loader\").remove();\r\n  const overlay = item.find(\".overlay\");\r\n  if (overlay.length > 0) {\r\n    overlay.remove();\r\n  }\r\n  item.removeClass(\"loading\");\r\n}\r\n\r\nfunction getUrl(endpoint, JSONmock = \"\") {\r\n  const { hostname, port, protocol } = window.location;\r\n\r\n  if (hostname === \"localhost\") {\r\n    if (port === \"4502\") {\r\n      return `${protocol}//${hostname}:${port}${endpoint}`;\r\n    } else {\r\n      return JSONmock;\r\n    }\r\n  } else {\r\n    return `${protocol}//${hostname}${endpoint}`;\r\n  }\r\n}\r\n\n\n//# sourceURL=webpack://aem-maven-archetype/./src/main/webpack/site/_util.js?");

/***/ })

/******/ 	});
/************************************************************************/
/******/ 	// The module cache
/******/ 	var __webpack_module_cache__ = {};
/******/ 	
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/ 		// Check if module is in cache
/******/ 		var cachedModule = __webpack_module_cache__[moduleId];
/******/ 		if (cachedModule !== undefined) {
/******/ 			return cachedModule.exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = __webpack_module_cache__[moduleId] = {
/******/ 			// no module.id needed
/******/ 			// no module.loaded needed
/******/ 			exports: {}
/******/ 		};
/******/ 	
/******/ 		// Execute the module function
/******/ 		__webpack_modules__[moduleId](module, module.exports, __webpack_require__);
/******/ 	
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/ 	
/************************************************************************/
/******/ 	/* webpack/runtime/compat get default export */
/******/ 	!function() {
/******/ 		// getDefaultExport function for compatibility with non-harmony modules
/******/ 		__webpack_require__.n = function(module) {
/******/ 			var getter = module && module.__esModule ?
/******/ 				function() { return module['default']; } :
/******/ 				function() { return module; };
/******/ 			__webpack_require__.d(getter, { a: getter });
/******/ 			return getter;
/******/ 		};
/******/ 	}();
/******/ 	
/******/ 	/* webpack/runtime/define property getters */
/******/ 	!function() {
/******/ 		// define getter functions for harmony exports
/******/ 		__webpack_require__.d = function(exports, definition) {
/******/ 			for(var key in definition) {
/******/ 				if(__webpack_require__.o(definition, key) && !__webpack_require__.o(exports, key)) {
/******/ 					Object.defineProperty(exports, key, { enumerable: true, get: definition[key] });
/******/ 				}
/******/ 			}
/******/ 		};
/******/ 	}();
/******/ 	
/******/ 	/* webpack/runtime/hasOwnProperty shorthand */
/******/ 	!function() {
/******/ 		__webpack_require__.o = function(obj, prop) { return Object.prototype.hasOwnProperty.call(obj, prop); }
/******/ 	}();
/******/ 	
/******/ 	/* webpack/runtime/make namespace object */
/******/ 	!function() {
/******/ 		// define __esModule on exports
/******/ 		__webpack_require__.r = function(exports) {
/******/ 			if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 				Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 			}
/******/ 			Object.defineProperty(exports, '__esModule', { value: true });
/******/ 		};
/******/ 	}();
/******/ 	
/************************************************************************/
/******/ 	
/******/ 	// startup
/******/ 	// Load entry module and return exports
/******/ 	// This entry module is referenced by other modules so it can't be inlined
/******/ 	var __webpack_exports__ = __webpack_require__("./src/main/webpack/site/main.ts");
/******/ 	
/******/ })()
;