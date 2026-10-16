"Pokrenuti nad Inspect -> Classes. Roassal 3: mapa kontrolera i servisa."
| classes data canvas x y |
classes := self select: [ :cls |
    (cls isStub not) and: [
        ((cls mooseName includesSubstring: '.controller.') or: [ cls mooseName includesSubstring: '.service.' ])
            and: [ cls methods size > 1 ]
    ]
].
data := classes collect: [ :cls |
    | methods shared total percent |
    methods := cls methods asArray.
    shared := 0.
    1 to: methods size do: [ :i |
        (i + 1) to: methods size do: [ :j |
            | attrs1 attrs2 |
            attrs1 := ((methods at: i) accesses collect: [ :a | a variable ]) asSet.
            attrs2 := ((methods at: j) accesses collect: [ :a | a variable ]) asSet.
            (attrs1 intersection: attrs2) isEmpty ifFalse: [ shared := shared + 1 ]
        ]
    ].
    total := methods size * (methods size - 1) / 2.
    percent := total = 0 ifTrue: [ 0 ] ifFalse: [ shared * 100.0 / total ].
    { cls mooseName copyAfterLast: $.. methods size. percent. cls }
].
data := data asSortedCollection: [ :a :b | a second > b second ].
canvas := RSCanvas new.
x := 0.
y := 0.
data do: [ :entry |
    | name count percent color box label |
    name := entry first.
    count := entry second.
    percent := entry third.
    color := (count >= 15 and: [ percent < 15 ])
        ifTrue: [ Color red muchLighter ]
        ifFalse: [ (count >= 15 and: [ percent < 30 ])
            ifTrue: [ Color orange muchLighter ]
            ifFalse: [ Color green muchLighter ] ].
    box := RSBox new.
    box size: 190 @ 54.
    box color: color.
    box borderColor: Color gray.
    box position: x @ y.
    canvas add: box.
    label := RSLabel new.
    label text: name.
    label fontSize: 10.
    label position: x @ (y - 10).
    canvas add: label.
    label := RSLabel new.
    label text: count asString, ' metoda | ', percent rounded asString, '%'.
    label fontSize: 10.
    label position: x @ (y + 10).
    canvas add: label.
    x := x + 225.
    x > 900 ifTrue: [ x := 0. y := y + 85 ]
].
canvas @ RSCanvasController.
canvas open
