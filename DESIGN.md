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

In tal caso, parte del file di configurazioni viene dedicata alla descrizione
dei tagli, e ogni entry ha un formato simile a questo:

```json
{
    "name": "Denomination Name",
    "value": 1,
    "itemId": "minecraft:item_id"
}
```

Se vogliamo tradurre la tabella dei tagli in quello che poi viene riportato su
un file di configurazioni, il risultato definitivo sarà molto simile al
seguente: (Nell'esempio si utilizza il formato JSON)

```json
{
    "denominations": [
        {
            "name": "Lapis Zenar",
            "value": 1,
            "itemId": "variedcommodities:coin_iron"
        },
        {
            "name": "Gold Zenar",
            "value": 8,
            "itemId": "variedcommodities:coin_gold"
        },
        {
            "name": "Diamond Zenar",
            "value": 64,
            "itemId": "variedcommodities:coin_diamond"
        },
        {
            "name": "Emerald Zenar",
            "value": 512,
            "itemId": "variedcommodities:coin_emerald"
        },
        {
            "name": "Ruby Zenar",
            "value": 4096,
            "itemId": "variedcommodities:coin_bronze"
        },
        {
            "name": "Platinum Zenar",
            "value": 32768,
            "itemId": "variedcommodities:coin_stone"
        }
    ]
}
```

L'inserimento di un nuovo taglio, o la rimozione di uno già esistente, così come
la modifica del valore di un taglio, o dell'oggetto associato si risolve dunque
nella manipolazione delle entry di questo file, senza agire direttamente sul
codice (ribadisco che le "vesti" del file di configurazioni potrebbero variare
a seconda del formato che verrà definitivamente utilizzato, JSON in questo caso
specifico.)

### Vincolo sulle entry

Non si impone un limite al numero di tagli presenti, ma si chiede che sia
necessariamente presente il taglio associato al valore 1. In caso di mancato
inserimento, il plugin lancerà un'eccezione. Questo vincolo sussiste perché in
caso di mancanza del valore unitario, alcuni valori potrebbero non essere
traducibili dal conto virtuale a quello fisico senza dovuti arrotondamenti.

## Comandi

```
/zenar withdraw [-d|--denomination taglio] <quantity>
/zenar deposit

# Se non ci sono conflitti

/ritira     (rimanda a /zenar withdraw)
/deposita   (rimanda a /zenar deposit)
```

### Operazione di prelievo

Nel rispetto dei vincoli specificati nella sezione successiva, il comando di
prelievo permette di ottenere zenar fisici dal proprio conto virtuale.
Perché l'operazione vada a buon fine, dev'essere specificata una quantità da
prelevare e, in questo caso, l'algoritmo interno deciderà autonomamente
quali tagli assegnare e la relativa quantità.

Per avere maggiore controllo sui tagli emessi, è possibile utilizzare l'opzione
_denomination_ (mediante flag -d o --denomination) e specificare il taglio
(attraverso l'identificativo o il valore dello stesso.) In tal caso, la quantità
specificata non indicherà il valore totale degli zenar emesse, ma il numero
stesso degli zenar di quel taglio.

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

> /zenar withdraw 123

# Oppure

> /ritira 123

# Preleva 20 Diamond Zenar dal conto. Al termine dell'operazione:
# - 20 Diamond Zenar
# Totale: 1280 Zenar

> /zenar withdraw -d diamond 20

# Oppure

> /ritira -d diamond 20

# (*) Distribuiti secondo un algoritmo interno casuale.
```
#### Casi speciali

Le operazioni di prelievo possono essere soggette a condizioni speciali, che ne
determinano l'esito. La gestione di tali casistiche viene effettuata dal plugin
nelle modalità che seguono:

<dl><dt>
L'utente prova a prelevare una somma di zenar superiore a quella posseduta
</dt><dd>
Il plugin prova a trasferire il tetto massimo di zenar trasferibili dal conto
virtuale. In caso di successo, l'utente viene notificato con la quota esatta di
zenar trasferiti dal proprio conto. (Non con quella richiesta)
</dd><dt>
L'utente prova a prelevare una certa somma di zenar, ma non ha spazio nell'
inventario a sufficienza per immagazzinarli
</dt><dd>
L'operazione di prelievo fallisce e l'utente ne viene notificato.
</dd><dt>
L'utente prova a prelevare una certa somma, ma l'inventario contiene già degli
zenar
</dt><dd>
Nell'implementazione, l'algoritmo interno dev'essere abbastanza intelligente da
gestire il merge nella maniera ottimale, e solo nel caso di reale conflitto,
rimandare al caso precedente.
</dd><dt>
L'utente prova a prelevare una somma negativa
</dt><dd>
L'operazione di prelievo fallisce e l'utente ne viene notificato.
</dd>
</dl>

### Operazione di deposito

Nel rispetto dei vincoli specificati nella sezione successiva, il comando di
deposito permette all'utente di depositare tutti gli zenar, presenti all'interno
del proprio inventario, sul proprio conto virtuale.

Al contrario dell'operazione precedente, il comando non accetta parametri.

Esempio

```
# All'interno dell'inventario del player sono presenti 5 Diamond Zenar e 2 Gold
# Zenar, per un totale di 336 Zenar. Sul conto virtuale sono presenti 200 Zenar.

> /zenar deposit

# Oppure

> /deposita

# Al termine dell'operazione, tutti gli zenar contenuti nell'inventario del
# player saranno rimossi. Il conto virtuale ammonterà a 536 Zenar.
```

### Timeout

Dal momento che le operazioni di prelievo/deposito potrebbero rappresentare un'
incognita sulla prestazioni, ci si riserva la possibilità di implementare una
finestra temporale (timeout) all'interno della quale non è possibile utilizzare
i comandi di prelievo/deposito.

## Vincoli

Non presenti.