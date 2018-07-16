new(company_name, company_ico, company_dico, company_address, company_website, company_phone,
customer_code, customer_name, customer_birthdate, customer_op_number, customer_address,
contract_code, contract_from_date, contract_to_date, contract_missing_fee, contract_days, contract_profit,
product_list, product_list_small, product_cards,
global_lower_price_days, global_storage_fee_in_percent, global_storage_fee_minimum, global_not_sold_fee_in_percent, global_not_sold_days_to_pay, global_withdrawal_fee
) ::= <<
----------------------------------------------------------------------------
		SMLOUVA O PRODEJI VĚCI ČÍSLO: <contract_code>
----------------------------------------------------------------------------

Firma: <company_name>   IČO: <company_ico>  DIČO: <company_dico>
      <company_address>   telefon: <company_phone>
      <company_website>
   jako obstaravatel a
zákazník č.: <customer_code>
   příjmení,jméno: <customer_name>  datum narození: <customer_birthdate>  číslo OP: <customer_op_number>
   adresa: 	<customer_address>
   jako odběratel

   uzavírají v souladu s ustanovením paragrafu 737 a násl. Občanského zákoníku tuto
   smlouvu:
1) Obstaravatel při podpisu smlouvy převzal od objednatele za účelem odsta-
   rání jejich prodeje tyto jemu svěřené věci:
 ----------------------------------------------------------------------------
  č.věci      název           popis                               kusů    mp
	  cena zák.    pr.cena     % sn.   sníž.c.z.   sn.pr.c     np      op
 ----------------------------------------------------------------------------
  <product_list>
 ----------------------------------------------------------------------------
   Obstaratel se zavazuje učinit potřebná opatření k prodeji převzatých věcí
2) Svěřené věci bude obstaratel nabízet k prodeji v dohodnutých provoznovnách
   R S nyní v provozovně: Rokycany od: <contract_from_date>
3) Doba prodeje svěřených věcí se sjednává na dobu <contract_days> dnů ode dne podpisu
   této smlouvy. t.zn. do <contract_to_date>
4) Dle výslovného přání objednatele bude obstaratel prodávat svěřené věci
   za ceny uvedené v seznamu čl. 1) smlouvy ve sloupci \<pr.cena>.
5) Objednatel nesjednal prodej věcí za cenu sníženou. Tuto možnost je možné
   dodatečně sjednat a to do termínu: <contract_to_date>
6) Za přípravu věcí k prodeji uhradí objednavatel obstarateli manipulační
   poplatek uvedený v čl. 1) smlouvy ve sloupci \<mp>.
   Za obstarání prodeje svěřených věcí bude odměna obstaratele činit částku:
   při prodjení ceně jedné věci do 3000 Kč: <contract_profit>% prodejní ceny a nad tuto
   prodejní cenu: <contract_profit>% prodejní ceny a prodejní ceny budou jak uvedeno
   v čl. 1) smlouvy ve sloupci \<pr.cena>.
7) V případě. že obstaravatel odstoupí od smlouvy před dohodnutou dobou
   určenou k prodeji uhradí obstarateli při převzetí věcí poplatek ve výši
   <global_withdrawal_fee> % prodejní ceny věci. t.zn. částky uvedené v čl. 1) ve sloupci \<op>.
8) Objednatel je povinen dostavit se do provozovny
   v případě věcí prodávaných s možností prodeje za snížené ceny:
  <product_list_small>
   nejpozději do >údaj K\<: <global_lower_price_days> dnů. ode dne ukončení prodeje. t.zn.,
   do >termínu M\<: <contract_to_date>
   ----------------------------
9) Odměnu za prodej svěřených věcí si obstaratel srazí od častky, za kterou
   veci prodal a zbylou část uvedenou v čl. 1) smlouvy ve sloupci \<cena zak.>,
   případně \<sniž.c.z.> vyplatí objednavateli při jeho osobní návštěvě
   v provozovně. Pokud se objednavatel nedostaví k obstarateli do termínu
   uvedeného v předchazejícím čl.8) smlouvy, je obstaratel oprávněn zaslat
   sjednanou častku za prodej věcí na jeho adresu uvedenou ve smlouvě s tím,
   že od této částky odečte náklady jím vynaložené na odeslání peněz.
10)Pokud nedojde k prodeji svěřených věcí, vydá objednavateli tyto věci a ten
   mu za jejich uložení po sjednanou dobu uhradí paušální úhradu účelne vynalo-
   žených nákladů ve výši <global_storage_fee_in_percent> % sjednané prodejní ceny, nejméne částku <global_storage_fee_minimum> Kč
   Částky jsou uvedeny v čl.1) ve sloupci \<np>.
10)Pokud nedojde k prodeji svěřených věcí a objednavatel se ve sjednané době
   nedostaví k jejich vyzvednutí, je povinen obstarateli zaplatit smluvní po-
   ve výši <global_not_sold_fee_in_percent> promile sjednané prodejní ceny vecí za kadžý den,
   kdy je v prodlení s jejich vyzvednutím po skončení platnosti smlouvy o ob-
   staráni prodeje. Smluvní pokutu je objednavatel povinen uhradit obstara-
   teli při převzetí věcí nebo do <global_not_sold_days_to_pay> dnů, ode dne jejich písemného vyúčtování.
   V případě nezaplacení paušální úhrady vynaložených nákladů dle čl. 10
   smlouvy nebo smluvní pokuty dle čl. 11 smlouvy je obstaratel oprávněn svě-
   řené věci zadržet až do úhrady techto peněžitých pohledávek za objednavate-
   lem. Obstaratel má nárok na náhradu nákladů účelně vynaložených na opatro-
   vání zadržených věcí.
12)Obstaratel je povinen po sjednanou dobu prodeje věcí či po dobu jejich
   zadržení dle čl.11 smlouvy pečovat o veci tak, aby nedolo k jejich poško-
   zení, zničení či ztrátě. Obstaratel neodpovídá objednavateli za škodu, kte-
   rá vznikne na svěřených věcích v důsledku jejich vady, přirozené povahy vě-
   ci, stáří či vadného obalu.
13)Objednavatel prohlašuje, že je výhradním vlastníkem vecí či že je
   oprávněn je prodat jako jeden ze spoluvlastniků, že sdelil obstarateli
   veškeré údaje, které se k prodávaným věcem vztahují včetne údajů o stáří
   věcí, zjevných a skrytých vad věcí jakož i údaje, které jsou
   podstatné k nerušnému splnění závazku obstaratele.
   Objednavatel odpovídá za škodu, která obstarateli vznikne v důsledku
   neuvedení všech podstatných skutečností, týkajících se prodávaných věcí.
14)Tato smlouva slouží jako potvrzení o převzetí věcí k obstarání
   jejich prodeje. Při nepředložení smlouvy se platí poplatek <contract_missing_fee> Kč.
15)Právní vztahy smluvních stran neupravené touto smlouvou se řídí občan-
   ským zákoníkem.
16)Smlouva je vyhotovena ve dvou vyhotoveních, z nichž každá ze smluvních
   stran obdrží po jednom.


   Rokycany dne: <contract_from_date>

   Objednatel: 				Obstaratel:

   podpis: 				podpis a razítko:



<product_cards>










>>