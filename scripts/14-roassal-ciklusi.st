
| edges pairs names canvas boxes positions |

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

pairs := Set new.

edges do: [ :edge |
    (edges includes: (edge value -> edge key))
        ifTrue: [
            pairs add:
                ({ edge key. edge value }
                    asSortedCollection asArray)
        ]
].

names := Set new.
pairs do: [ :pair |
    names addAll: pair
].

canvas := RSCanvas new.
boxes := Dictionary new.
positions := {
    0 @ 0.
    260 @ 0.
    0 @ 150.
    260 @ 150
}.

names asSortedCollection withIndexDo: [ :name :index |
    | box label position |

    position := positions at: index.

    box := RSBox new.
    box size: 210 @ 55.
    box color: Color lightBlue.
    box borderColor: Color gray.
    box position: position.
    canvas add: box.

    label := RSLabel new.
    label text: (name copyAfterLast: $.).
    label fontSize: 11.
    label position: position.
    canvas add: label.

    boxes at: name put: box
].

pairs do: [ :pair |
    | line |

    line := RSLine new.
    line from: (boxes at: pair first).
    line to: (boxes at: pair second).
    line color: Color red.
    canvas add: line
].

canvas @ RSCanvasController.
canvas open
