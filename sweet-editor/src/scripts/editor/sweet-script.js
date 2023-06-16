import * as monaco from 'monaco-editor';
import CompletionItemProvider from './completion.js';

export const initializeSweetScript = () => {
    const language = 'javascript';
    // 设置代码提示
    monaco.languages.registerCompletionItemProvider(language, CompletionItemProvider);
}
