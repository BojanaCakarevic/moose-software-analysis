"Pokrenuti nad Inspect -> Invocations. Jedinstvene izlazne, ulazne i ukupno povezane klase."
| edges names results |
edges := Set new.
self do: [ :inv |
    inv sender ifNotNil: [ :sender |
        sender parentType ifNotNil: [ :source |
            inv candidates do: [ :method |
                method parentType ifNotNil: [ :target |
                    (source ~= target and: [ source isStub not and: [ target isStub not ] ])
                        ifTrue: [ edges add: (source mooseName -> target mooseName) ]
                ]
            ]
        ]
    ]
].
names := #( 'BookListController' 'DashboardController' 'MyBookController' 'RightPanelController' 'BookDetailController' ).
results := OrderedCollection new.
names do: [ :name |
    | outgoing incoming union |
    outgoing := Set new.
    incoming := Set new.
    edges do: [ :edge |
        (edge key endsWith: ('.', name)) ifTrue: [ outgoing add: edge value ].
        (edge value endsWith: ('.', name)) ifTrue: [ incoming add: edge key ]
    ].
    union := outgoing union: incoming.
    (outgoing isEmpty and: [ incoming isEmpty ]) ifFalse: [
        results add: { name. outgoing size. incoming size. union size }
    ]
].
results asArray
