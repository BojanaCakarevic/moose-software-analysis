| names |

names := #(
    'BookListController'
    'MyBookController'
    'DashboardController'
    'RightPanelController'
).

(self flatCollect: [ :inv |
    inv sender
        ifNil: [ #() ]
        ifNotNil: [ :sender |
            sender parentType
                ifNil: [ #() ]
                ifNotNil: [ :source |
                    inv candidates
                        select: [ :method |
                            method parentType notNil
                                and: [
                                    (names includes: source name)
                                    and: [
                                        (names includes: method parentType name)
                                        and: [
                                            source name ~= method parentType name
                                            and: [
                                                (source name = 'RightPanelController')
                                                or: [
                                                    method parentType name = 'RightPanelController'
                                                ]
                                            ]
                                        ]
                                    ]
                                ]
                        ]
                        thenCollect: [ :method |
                            sender mooseName -> method mooseName
                        ]
                ]
        ]
]) asSet asSortedCollection: [ :a :b |
    a key < b key or: [
        a key = b key and: [ a value < b value ]
    ]
]