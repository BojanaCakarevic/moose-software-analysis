| edges |

edges := Set new.

self do: [ :inv |
    inv sender ifNotNil: [ :sender |
        sender parentType ifNotNil: [ :source |
            inv candidates do: [ :method |
                method parentType ifNotNil: [ :target |
                    (source ~= target
                        and: [ source isStub not
                        and: [ target isStub not ] ])
                        ifTrue: [
                            edges add: (source mooseName -> target mooseName)
                        ]
                ]
            ]
        ]
    ]
].

(edges select: [ :edge |
    (edge key copyAfterLast: $.) = 'RightPanelController'
]) collect: [ :edge |
    edge value copyAfterLast: $.
]