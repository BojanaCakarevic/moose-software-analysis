self collect: [ :pair |
    (pair key copyAfterLast: $.)
        , ' -> '
        , (pair value copyAfterLast: $.)
]