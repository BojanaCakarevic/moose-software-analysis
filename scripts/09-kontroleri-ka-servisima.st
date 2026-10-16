"Pokrenuti nad Inspect -> Invocations. Za svakog kontrolera: broj kandidata poziva, jedinstvenih servisnih metoda i servisnih klasa."
| stats |
stats := Dictionary new.
self do: [ :inv |
    inv sender ifNotNil: [ :sender |
        sender parentType ifNotNil: [ :source |
            ((source isStub not) and: [ source mooseName includesSubstring: '.controller.' ]) ifTrue: [
                inv candidates do: [ :method |
                    method parentType ifNotNil: [ :target |
                        ((target isStub not) and: [ target mooseName includesSubstring: '.service.' ]) ifTrue: [
                            | record |
                            record := stats at: source mooseName ifAbsentPut: [ { 0. Set new. Set new } ].
                            record at: 1 put: (record first + 1).
                            record second add: method mooseName.
                            record third add: target mooseName
                        ]
                    ]
                ]
            ]
        ]
    ]
].
(stats keys asSortedCollection) collect: [ :name |
    | record |
    record := stats at: name.
    { name copyAfterLast: $.. record first. record second size. record third size }
]
