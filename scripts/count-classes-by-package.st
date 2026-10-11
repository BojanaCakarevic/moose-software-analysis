Pokrenuti u Moose Inspector-u nad grupom
 projektnih klasa (bez stubova).

| packages |
packages := #('api' 'config' 'constants' 'controller'
              'dao' 'exception' 'item' 'model'
              'service' 'singleton' 'utils').

packages collect: [ :package |
    package -> (self count: [ :each |
        each mooseName includesSubstring: ('.', package, '.')
    ])
]