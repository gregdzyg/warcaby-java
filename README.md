
# Warcaby Online (Java Swing)

Dwuosobowa gra w warcaby działająca w trybie online, w całości napisana w **Javie** z wykorzystaniem biblioteki **Swing** do interfejsu graficznego.  
Gra umożliwia rozgrywkę w czasie rzeczywistym pomiędzy dwoma klientami, opartą o **architekturę klient-serwer**.

---

## 🎯 Funkcje
- **Rozgrywka online dla dwóch graczy** przez gniazda TCP.
- **Graficzny interfejs użytkownika** oparty na Swing.
- **Pełna logika gry**:
    - Sprawdzanie poprawności ruchów.
    - Wielokrotne bicie w jednej turze.
    - Promocja na damkę.
    - Wymuszanie bicia.
- **Architektura klient-serwer**:
    - Oddzielny serwer obsługujący połączenia i synchronizację stanu gry.
    - Oddzielna aplikacja kliencka dla każdego gracza.

---

## 🛠️ Technologie
- **Java 17**
- **Java Swing** – GUI
- **Java Sockets** – komunikacja sieciowa
- **Programowanie obiektowe (OOP)**

---

## 📂 Struktura projektu
src/
├── client/ # Logika klienta + GUI
├── server/ # Logika serwera
├── model/ # Zasady gry, plansza, pionki
└── META-INF/ # Plik manifestu

---

## 🚀 Jak uruchomić
1. **Skompiluj projekt** w IntelliJ IDEA (netBeans) lub przy pomocy `javac`.
2. **Uruchom serwer**:
   ```bash
   java server.Server
Uruchom dwóch klientów (każdy w osobnym procesie):
java client.GameClient
Gra gotowa do rozpoczęcia!

📸 Prezentacja

(Zrzuty ekranu i/lub nagranie wideo zostaną dodane w kolejnym commicie)

📜 Licencja

Projekt udostępniony na licencji MIT – do dowolnego użytku i modyfikacji.

👤 Autor

Projekt stworzony przez Grzegorza Dzyga w ramach zajęć programowania w Javie na studiach.


