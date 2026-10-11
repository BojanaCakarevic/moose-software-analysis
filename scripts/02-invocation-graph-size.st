| edges classes |

edges := Set new.
classes := Set new.

self do: [ :inv |
    inv sender ifNotNil: [ :sender |
        sender parentType ifNotNil: [ :source |
            inv candidates do: [ :method |
                method parentType ifNotNil: [ :target |
                    (source ~= target
                        and: [ source isStub not
                        and: [ target isStub not ] ])
                        ifTrue: [
                            edges add: (source mooseName -> target mooseName).
                            classes add: source mooseName.
                            classes add: target mooseName
                        ]
                ]
            ]
        ]
    ]
].

Array
    with: classes size
    with: edges size