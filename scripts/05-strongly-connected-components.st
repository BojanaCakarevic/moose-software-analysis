| edges nodes reach components remaining start visited frontier next |

edges := Set new.
nodes := Set new.

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
                            nodes add: source mooseName.
                            nodes add: target mooseName
                        ]
                ]
            ]
        ]
    ]
].

reach := Dictionary new.

nodes do: [ :node |
    visited := Set with: node.
    frontier := Set with: node.

    [ frontier isEmpty ] whileFalse: [
        next := Set new.

        edges do: [ :edge |
            ((frontier includes: edge key)
                and: [ (visited includes: edge value) not ])
                ifTrue: [
                    next add: edge value.
                    visited add: edge value
                ]
        ].

        frontier := next
    ].

    reach at: node put: visited
].

components := OrderedCollection new.
remaining := nodes copy.

[ remaining isEmpty ] whileFalse: [
    start := remaining anyOne.

    visited := (reach at: start) select: [ :node |
        (reach at: node) includes: start
    ].

    visited size > 1 ifTrue: [
        components add: visited asSortedCollection asArray
    ].

    remaining removeAll: visited
].

components asArray collect: [ :group |
    group collect: [ :className |
        className copyAfterLast: $.
    ]
]