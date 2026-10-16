"Pokrenuti nad Inspect -> Invocations. Broj kandidata metoda po smeru."
| pairs results |
pairs := {
    { '.controller.'. '.service.' }.
    { '.service.'. '.dao.' }.
    { '.controller.'. '.dao.' }.
    { '.service.'. '.controller.' }.
    { '.dao.'. '.service.' }.
    { '.dao.'. '.controller.' }
}.
results := OrderedCollection new.
pairs do: [ :pair |
    | count |
    count := 0.
    self do: [ :inv |
        inv sender ifNotNil: [ :sender |
            sender parentType ifNotNil: [ :source |
                ((source isStub not) and: [ source mooseName includesSubstring: pair first ]) ifTrue: [
                    inv candidates do: [ :method |
                        method parentType ifNotNil: [ :target |
                            ((target isStub not) and: [ target mooseName includesSubstring: pair second ])
                                ifTrue: [ count := count + 1 ]
                        ]
                    ]
                ]
            ]
        ]
    ].
    results add: { pair first. pair second. count }
].
results asArray
