import tsParser from "@typescript-eslint/parser";
import path from "node:path";
import { fileURLToPath } from "node:url";
import js from "@eslint/js";
import { FlatCompat } from "@eslint/eslintrc";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const compat = new FlatCompat({
    baseDirectory: __dirname,
    recommendedConfig: js.configs.recommended,
    allConfig: js.configs.all
});

export default [{
    ignores: ["../ui.apps/", "./src/main/webpack/tests/"],
}, ...compat.extends("plugin:@typescript-eslint/recommended"), {
    languageOptions: {
        parser: tsParser,
        ecmaVersion: 2018,
        sourceType: "module",
    },

    rules: {
        curly: 1,
        "@typescript-eslint/explicit-function-return-type": [0],
        "@typescript-eslint/no-explicit-any": [0],
        "ordered-imports": [0],
        "object-literal-sort-keys": [0],
        "max-len": [1, 120],
        "new-parens": 1,
        "no-bitwise": 1,
        "no-cond-assign": 1,
        "no-trailing-spaces": 0,
        "eol-last": 1,

        "func-style": ["error", "declaration", {
            allowArrowFunctions: true,
        }],

        semi: 1,
        "no-var": 0,
    },
}];