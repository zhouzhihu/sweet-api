import * as monaco from 'monaco-editor'

async function completionScript(suggestions, line) {
    if(line.endsWith(".")){
        let command = line.substring(0, line.indexOf("."))
        console.log(command)
        console.log(eval("defSuggestions." + command + ".table"))
        suggestions.push(eval("defSuggestions." + command + ".table"))
    }
}

const quickSuggestions = [
    ['bre', 'break;', '跳出循环'],
    ['con', 'continue;', '继续循环'],
    ['if', 'if (${1:condition}){\r\n\t$2\r\n}', '判断'],
    ['ife', 'if (${1:condition}) {\r\n\t$2\r\n} else { \r\n\t$3\r\n}', '判断'],
    ['fori', 'for (item in ${1:collection}) {\r\n\t$2\r\n}', 'for...in循环集合'],
    ['forof', 'for (let item of ${1:collection}) {\r\n\t$2\r\n}', 'for...of循环集合'],
    ['exit', 'return this.request.exit(${1:code}, ${2:message});', '退出'],
    ['logi', 'this.log.info($1);', 'info日志'],
    ['logd', 'this.log.debug($1);', 'debug日志'],
    ['loge', 'this.log.error($1);', 'error日志'],
    ['logw', 'this.log.warn($1);', 'warn日志']
]

const keyWord = []

const defSuggestions = {
    roots: ["request", "response", "header", "path", "body", "session", "cookie", "context", "entity", "log", "db", "userService"],
    db: {
        methods: [
            {
                table: {
                    sortText: "1",
                    label: "table(TableName)",
                    detail: "指定表访问",
                    insertText: "table(${1:TableName})",
                    kind: monaco.languages.CompletionItemKind.Method,
                    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                    methods: [
                        {
                            where: {
                                sortText: "1",
                                label: "where()",
                                detail: "拼接where条件",
                                insertText: "where()",
                                kind: monaco.languages.CompletionItemKind.Method,
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                
                            },
                            select: {
                                sortText: "2",
                                label: "select()",
                                detail: "执行select查询",
                                insertText: "select()",
                                kind: monaco.languages.CompletionItemKind.Method,
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,

                            }
                        }
                    ]
                },
            },
            {
                ds: {
                    sortText: "2",
                    label: "ds(DataSourceName)",
                    detail: "指定数据源访问",
                    insertText: "ds(${1:DataSourceName})",
                    kind: monaco.languages.CompletionItemKind.Method,
                    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                    ref: "db"
                }
            },
            {
                select: {
                    sortText: "2",
                    label: "select(sql)",
                    detail: "查询SQL,返回List类型结果",
                    insertText: "select(${1:sql})",
                    kind: monaco.languages.CompletionItemKind.Method,
                    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet
                }
            },
            {
                selectOne: {
                    sortText: "3",
                    label: "selectOne(sql)",
                    detail: "查询单条结果,查不到返回null",
                    insertText: "selectOne(${1:sql})",
                    kind: monaco.languages.CompletionItemKind.Method,
                    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet
                }
            }
        ]
    }
}

const CompletionItemProvider = {
    provideCompletionItems: async function (model, position) {
        let value = model.getValueInRange({
            startLineNumber: 1,
            startColumn: 1,
            endLineNumber: position.lineNumber,
            endColumn: position.column
        });
        let line = model.getValueInRange({
            startLineNumber: position.lineNumber,
            startColumn: 1,
            endLineNumber: position.lineNumber,
            endColumn: position.column
        });
        let word = model.getWordUntilPosition(position);
        let range = {
            startLineNumber: position.lineNumber,
            endLineNumber: position.lineNumber,
            startColumn: word.startColumn,
            endColumn: word.endColumn
        }
        let incomplete = false;
        let suggestions = quickSuggestions.map(item => {
            return {
                label: item[0],
                kind: monaco.languages.CompletionItemKind.Struct,
                detail: item[2] || item[1],
                insertText: item[1],
                filterText: item[0],
                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                range
            }
        });
        // if (line.length > 1) {
        //     await completionScript(suggestions, line)
        // }
        return { suggestions, incomplete }
    },
    triggerCharacters: ['.', ':']
};
export default CompletionItemProvider
