| edges cycles |

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

cycles := Set new.

edges do: [ :edge |
    (edges includes: (edge value -> edge key))
        ifTrue: [
            cycles add:
                ((Array with: edge key with: edge value)
                    asSortedCollection asArray)
        ]
].

cycles asArray