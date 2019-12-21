Zenar Transactions
==================

Il plugin nasce con lo scopo di gestire eventuali transazioni tra conto virtuale
dell'utente e zenar fisici (intesi come items stackabili presenti in gioco.) Le
seguenti linee guida formalizzano le funzionalità che verranno messe a
disposizione dell'utente finale.

**Importante** Lo stato attuale del documento è da considerarsi WIP (Work in
Progress) e tutte le specifiche qui elencate potrebbero non rispecchiarne la
versione finale.

## Motivazioni

**Pratiche**: Per alcune mod presenti l'emissione o il deposito di zenar fisici
sarebbe l'unico modo al momento per interagire con l'utente nell'eventualità di
transazioni.

**Tecniche**: Affidarsi completamente ad una moneta virtuale significherebbe
dover risolvere problemi di compatibilità e adattamento con tutte le mod
presenti e future che non supportano la EconomyAPI di Sponge.

## Caratteristiche

Gli zenar fisici sono presenti in sei tagli diversi, e il valore sarà sempre un
multiplo di 8:

| Valore  	| Nome               | Item ID                        |
|--------------:|--------------------|--------------------------------|
| 1             | Lapis Zenar        | variedcommodities:coin_iron    |
| 8             | Gold Zenar         | variedcommodities:coin_gold    |
| 64            | Diamond Zenar      | variedcommodities:coin_diamond |
| 512           | Emerald Zenar      | variedcommodities:coin_emerald |
| 4096          | Ruby Zenar         | variedcommodities:coin_bronze  |
| 32768         | Platinum Zenar     | variedcommodities:coin_stone   |

Si valuta la possibilità di rendere tagli e relativi riferimenti _softcoded_.
In contrapposizione alla filosofia _hardcoded_, questa tabella di riferimento
diventerebbe ampiamente personalizzabile attraverso un file di configurazioni.

## Comandi

```
/zenar [-w|--withdraw] [-D|--denomination taglio] <quantity>
/zenar [-d|--deposit]
```

### Operazione di prelievo

Nel rispetto dei vincoli specificati nella sezione successiva, il comando di
prelievo permette di ottenere zenar fisici dal proprio conto virtuale.
Perché l'operazione vada a buon fine, dev'essere specificata una quantità da
prelevare e, in questo caso, l'algoritmo interno deciderà autonomamente
quali tagli assegnare e la relativa quantità.

Per avere maggiore controllo sui tagli emessi, è possibile utilizzare l'opzione
_denomination_ e specificare il taglio (attraverso l'identificativo o il valore
dello stesso.) In tal caso, la quantità specificata non indicherà il valore
totale degli zenar emesse, ma il numero stesso degli zenar di quel taglio.

Una lista degli identificativi potrebbe essere la seguente: lapis, gold,
diamond, emerald, ruby, platinum.

Esempi

```
# Preleva 123 zenar dal conto. Al termine dell'operazione l'utente avrà in
# inventario: (*)
# - 1 Diamond Zenar
# - 7 Gold Zenar
# - 3 Lapis Zenar
# Totale: 123 Zenar

> /zenar -w 123

# Preleva 20 Diamond Zenar dal conto. Al termine dell'operazione:
# - 20 Diamond Zenar
# Totale: 1280 Zenar

> /zenar --withdraw -D diamond 20

# (*) Distribuiti secondo un algoritmo interno casuale.
```

### Operazione di deposito

Nel rispetto dei vincoli specificati nella sezione successiva, il comando di
deposito permette all'utente di depositare tutti gli zenar, presenti all'interno
del proprio inventario, sul proprio conto virtuale.

Al contrario dell'operazione precedente, il comando non accetta parametri.

Esempio

```
# All'interno dell'inventario del player sono presenti 5 Diamond Zenar e 2 Gold
# Zenar, per un totale di 336 Zenar. Sul conto virtuale sono presenti 200 Zenar.

> /zenar -d

# Al termine dell'operazione, tutti gli zenar contenuti nell'inventario del
# player saranno rimossi. Il conto virtuale ammonterà 536 Zenar.
```

### Timeout

Dal momento che le operazioni di prelievo/deposito potrebbero rappresentare un'
incognita sulla prestazioni, ci si riserva la possibilità di implementare una
finestra temporale (timeout) all'interno della quale non è possibile utilizzare
i comandi di prelievo/deposito.

## Vincoli

Non presenti.