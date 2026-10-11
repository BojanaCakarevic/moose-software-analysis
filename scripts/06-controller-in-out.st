| edges names |

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
                            edges add:
                                (source mooseName -> target mooseName)
                        ]
                ]
            ]
        ]
    ]
].

names := #(
    'BookListController'
    'DashboardController'
    'MyBookController'
    'RightPanelController'
).

names collect: [ :name |
    | incoming outgoing |

    incoming := edges count: [ :edge |
        (edge value copyAfterLast: $.) = name
    ].

    outgoing := edges count: [ :edge |
        (edge key copyAfterLast: $.) = name
    ].

    Array
        with: name
        with: incoming
        with: outgoing
]