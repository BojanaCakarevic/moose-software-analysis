"Pokrenuti nad Inspect -> Classes. Prilagodjena metrika zajednickog pristupa atributima, NIJE LCOM."
| names results |
names := #( 'BookDetailController' 'BookManagementController' 'AccountManagementController' 'BookService' 'BookListController' ).
results := OrderedCollection new.
names do: [ :name |
    | cls methods shared total percent |
    cls := self detect: [ :each | each mooseName endsWith: ('.', name) ] ifNone: [ nil ].
    cls ifNotNil: [
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
        results add: { name. methods size. shared. total. percent rounded }
    ]
].
results asArray
