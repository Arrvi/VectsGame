# Vects, online game / Gra sieciowa w wektorki

**Projekt numer 1 na przedmiot *Sieci komputerowe i programowanie sieciowe w języku Java*** 

Autor: Kristian Kann, *s11531, grupa 11c*

Wersja 1.0, 16 listopada 2014

## Opis aplikacji

Aplikacja *Vects* to implementacja gry *Wektorki* (nie wiem, czy jest to oficjalna nazwa), w którą grywałem z moim tatą. Gra polega na wyścigu wirtualnych pojazdów na torze opartym na siatce. Podczas swojej tury gracz może przesunąć swój pojazd zgodnie z wektorem prędkości odchylając go w granicach `[-1, 1]` (aplikacja pozwala na zwiększenie możliwego odchylenia) oby składowych wektora, tj. ma do wyboru 9 sąsiadujących pól do wyboru (z wykluczeniem pól na których znajdują się inne pojazdy). Pozwala to na zaskakująco realistyczną symulację rzeczywistego wyścigu. Mechanika gry nie pozwala na tor pojazdu, który w rzeczywistości powodował utratę przyczepności lub przekroczenie osiągów silnika, co na przykład oznacza, że należy zwalniać przed zakrętami, aby nie wypaść z trasy.

Aplikacja jest napisana w języku angielskim bez użycia API lokalizacji (w związku z tym, że znacząca większość języków programowania jest oparta na języku angielskim, preferuję komunikaty także po angielsku, aby *nie przełączać się* w trakcie pisania kodu)  


## Wymagania, instalacja i uruchomienie

Aplikacja nie wymaga instalacji. Do uruchomienia wymagana jest Java 7 lub wyższa. Klasy główne to `eu.arrvi.vects.server.VectsServer` oraz `eu.arrvi.vects.client.VectsClient`. Wywołują je załączone skrypty `server.sh` oraz `client.sh` (skrypty nie były testowane - mają szansę nie działać). Zarówno klient jak i serwer wymagają wstępnej konfiguracji. Parametry wprowadza się w oknie po uruchomieniu każdego z elementów.

Serwer zaczyna nasłuchiwać po kliknięciu przycisku *Start server*. Klient wymaga kliknięcia *Connect* a następnie *Ready*.

Elementy toru oznaczone są kolorami:

* **Zielony** - początek toru / start. Gracze zaczynają w losowych punktach tego obszaru.
* **Czerwony** - koniec toru / meta. Pierwszy gracz, który wjedzie na ten obszar wygrywa i kończy wyścig (pozostałe miejsca nie są uwzględnianie).
* **Biały** (oraz czerwony i zielony) - tor
* **Czarny** - obszar poza torem. Wjechanie na ten kolor oznacza przegraną

W momencie rozpoczęcia ruchu na planszy wyświetlają się niewielkie okręgi będące możliwymi celami ruchu. Kliknięcie któregoś z nich oznacza wykonanie danego ruchu. Brak okręgów oznacza, że nie wszyscy gracze są gotowi lub inny gracz wykonuje ruch. Pokonana trasa wyświetlana jest jako zielona linia (szare linie to trasy innych graczy).

Liczy się kolor pod punktami pojazdów - trasa może przecinać czarne obszary.


## Różnice w stosunku do specyfikacji projektu (w tym znane błędy)

**Ad. 1.** Aplikacja działa w środowisku graficznym z użyciem biblioteki *Swing*. Nie ma wersji tekstowej.

**Ad. 2.a.** Aplikacja pozwala na rozgrywkę dowolnej liczby graczy. Ilość graczy ustala się po uruchomieniu serwera (domyślnie 2).

**Ad. 2.a.v.** Serwer nie zamyka się po zakończeniu wyścigu *(funkcjonalność planowana)*

**Ad. 2.b.i.** Aplikacja nie implementuje nazw użytkowników. Jako identyfikator używany jest numer portu gniazda każdego z graczy. *(funkcjonalność planowana)*

**Ad. 2.b.iii** Klient nie odłącza się od serwera po zakończeniu gry. *(funkcjonalność planowana)*


## Opis protokołu

Komunikacja pomiędzy klientem a serwerem odbywa się za pomocą pojedynczego połączenia `TCP`, domyślnie na porcie `9595`. Nie występują połączenia między klientami. Wszystkie komendy zaczynają się od 3 znaków identyfikujących komendę (bez rozróżniania wielkości liter) oraz opcjonalnie dowolnego znaku (dla czytelności spacji) i argumentów komendy oraz kończą się znakiem zakończenia linii `\r\n`. Zazwyczaj elementy tablicowe rozdzielane są za pomocą znaku `|`, a składowe elementów za pomocą `;` lub `,`.

### Komendy serwera dla klienta

* `NOP` - nie rób nic (nie używane, potencjalnie może utrzymywać połączenie)
* `ACK [info]` - zrozumiałem (używane do potwierdzenia niektórych działań klienta)
	* `info` - informacja dodatkowa
* `TRK path;res` - informacja o torze
	* `path` - ścieżka do pliku toru (na potrzeby wyświetlania)
	* `res` - gęstość siatki (liczba całkowita)
* `POS id;x,y|id2;x,y|...` - pozycje wszystkich pojazdów na planszy
	* `id` - identyfikator pojazdu
	* `x,y` - pozycja
* `TAR x,y|x,y|...` - możliwe cele ruchu, informacja o rozpoczęciu ruchu gracza
	* `x,y` - możliwy cel ruchu
* `DEN [reason]` - informacja o odmowie wykonania działania
	* `reason` - powód odmowy
* `WIN info` oraz `LOS info` - informacja o wygranej lub przegranej
	* `info` - powód zakończenia rozgrywki
* `ECH` - odpowiedz (protokół `ECHO`; używane przy testowaniu)
* `CHT id;msg` - wiadomość na chacie
	* `id` - id autora
	* `msg` - wiadomość

### Komendy klienta dla serwera
* `NOP`, `ECH`, `ACK`, `DEN` - j.w.
* `RDY` - gotowy do gry. Klient, który nie wysłał tej komendy jest traktowany jako obserwator. Po otrzymaniu tej komendy od odpowiedniej liczby klientów, serwer rozpoczyna rozgrywkę.
* `NOT` - odwołanie komendy `RDY` *(komenda nie rozwijana - jej użycie może powodować nieoczekiwane rezultaty)
* `BYE` - zakończenie połączenia
* `MOV x,y` - wykonanie ruchu na pozycję `x,y` (ruch jest sprawdzany przez serwer pod kątem poprawności)
* `CHT msg` - wiadomość na czacie (serwer wysyła ją do wszystkich połączonych klientów)
 
Wszystkie komendy które nie pasują do schematu traktowane są jako wiadomość na czacie.


## Pozostałe znane błędy

* W niektórych przypadkach, po zakończeniu wyścigu, treść czatu w aplikacji klienckiej jest czyszczona.
* Kod programu jest w części chaotyczny i wymaga przepisania. Pierwotna koncepcja nie przewidywała użycia modelu zdarzeniowego, który powinien był zostać użyty. Kolejność metod w niektórych klasach jest nieodpowiednia. Konwersja współrzędnych punktów na współrzędne używane do rysowania jest niespójna.
* Aplikacja kliencka nie implementuje powiększania planszy, które byłoby praktyczne przy większych gęstościach siatki.
* Jeżeli cele ruchu znajdują się poza torem, nie można się do nich przemieścić mimo, że serwer na to pozwala.
* Aplikacja kliencka wyświetla tor dopiero po naciśnięciu przycisku *Ready*, przez co nie może stać się obserwatorem.
* Nie można cofnąć gotowości do wyścigu (nie można wykonać komendy `NOT`)
* Nie ma możliwości przesłania pliku toru drogą sieciową. Wszystkie wymagane pliki muszą być dostarczone wraz z aplikacją.
* Serwer wysyła absolutną ścieżkę do pliku (w praktyce ogranicza to możliwość gry na kilku różnych maszynach).
* Czasem aplikacja ma problemy z uruchomieniem się na innych maszynach (nie udało się zidentyfikować problemu).
* Przyciski celów pozostawiają artefakty, jeżeli są poza granicami toru.


## Komentarz końcowy

Gra działa. Zazwyczaj. Tyle mogę powiedzieć. Zawiera sporo niedoróbek, napisana jest niezbyt pięknym stylem, ale uważam, że realizuje zadanie w większości. Wymaga jeszcze kilku dni pracy.

Część rozwiązań jest zapożyczona z internetu (w szczególności [http://stackoverflow.com/](http://stackoverflow.com/)), ale mogę zapewnić, że kod jest napisany przeze mnie bez pomocy osób trzecich.  