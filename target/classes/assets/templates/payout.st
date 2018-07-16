payout(

company_name, company_ico, company_dico, company_address, company_website, company_phone,
customer_code, customer_name, customer_birthdate, customer_op_number, customer_address,
payout_contract_code, income_contract_code, completion_contract_code, payout_sum, income_sum, total_fee, current_date,
returned_products_list

) ::= <<

Firma: <company_name>   IČO: <company_ico>  DIČO: <company_dico>
      <company_address>   telefon: <company_phone>
      <company_website>
----------------------------------------------------------------------------
		VÝDAJOVÝ POKLADNÍ DOKLAD č.: <payout_contract_code>
----------------------------------------------------------------------------
zákazník č.: <customer_code>
   příjmení,jméno: <customer_name>  datum narození: <customer_birthdate>  číslo OP: <customer_op_number>
   adresa: 	<customer_address>

přijal k dokladu celkovou odměnu za prodané věci:               <payout_sum> Kč
----------------------------------------------------------------------------
VYDÁNÍ POKLADNA CELKEM:                                         <payout_sum> Kč

Rokycany dne: <current_date>

Razítko a podpis firma:                           podpis (razítko) zákazník:


Firma: <company_name>   IČO: <company_ico>  DIČO: <company_dico>
      <company_address>   telefon: <company_phone>
      <company_website>
----------------------------------------------------------------------------
		PŘÍJMOVÝ POKLADNÍ DOKLAD č.: <income_contract_code>
----------------------------------------------------------------------------
zákazník č.: <customer_code>
   příjmení,jméno: <customer_name>  datum narození: <customer_birthdate>  číslo OP: <customer_op_number>
   adresa: 	<customer_address>

zaplatil k dokladu 
poplatek za uložení věci při jejich neprodání:                  <income_sum> Kč
----------------------------------------------------------------------------
PŘÍJEM POKLADNA CELKEM:                                         <income_sum> Kč

Rokycany dne: <current_date>

Razítko a podpis firma:                           podpis (razítko) zákazník:


Firma: <company_name>   IČO: <company_ico>  DIČO: <company_dico>
      <company_address>   telefon: <company_phone>
      <company_website>
zákazník č.: <customer_code>
   příjmení,jméno: <customer_name>  datum narození: <customer_birthdate>  číslo OP: <customer_op_number>
   adresa: 	<customer_address>
----------------------------------------------------------------------------
		DOKLAD O UKONČENÍ PRODEJE č.: <completion_contract_code>
----------------------------------------------------------------------------
   Mezi obstaratelem - výše uvedenou firmou a objednavatelem - výše uvedeným 
zákazníkem bylo ukončeno pro dále uvedená čísla zboží a dále uvedená čísla smluv
obstarání prodeje věci z důvodu: 

VRÁCENÍ ZBOŽÍ

Zákazník <current_date> převzal v pořádku zpět následující neprodané položky zboží:
----------------------------------------------------------------------------
  č.věci      název           popis                               kusy  č.sml.
	  přijato    ter.prod    ter. vy               popl. za ul.
	  placené poplatky
----------------------------------------------------------------------------
<returned_products_list>
----------------------------------------------------------------------------
Celkem poplatek za uložení věci:			              <total_fee> Kč
Pokladní doklad: <income_contract_code>

Rokycany dne: <current_date>

Razítko a podpis firma:                           podpis (razítko) zákazník:






>>