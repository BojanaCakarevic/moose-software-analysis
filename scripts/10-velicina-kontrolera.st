"Pokrenuti nad Inspect -> Classes. Samo glavne (ne-anonimne) klase sa sufiksom Controller."
| controllers sorted |
controllers := self select: [ :cls |
    (cls isStub not) and: [ cls mooseName endsWith: 'Controller' ]
].
sorted := controllers asSortedCollection: [ :a :b | a methods size > b methods size ].
{ controllers size. (controllers sum: [ :cls | cls methods size ]).
    (sorted collect: [ :cls | { cls mooseName copyAfterLast: $.. cls methods size } ]) asArray }
