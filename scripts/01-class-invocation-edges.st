| edges |

edges := Set new.

self do: [ :inv |
    inv sender ifNotNil: [ :sender |
        sender parentType ifNotNil: [ :senderClass |
            inv candidates do: [ :method |
                method parentType ifNotNil: [ :targetClass |
                    (senderClass ~= targetClass
                        and: [ senderClass isStub not
                        and: [ targetClass isStub not ] ])
                        ifTrue: [
                            edges add:
                                (senderClass mooseName -> targetClass mooseName)
                        ]
                ]
            ]
        ]
    ]
].

edges asSortedCollection: [ :a :b |
    a key < b key or: [
        a key = b key and: [ a value < b value ]
    ]
]