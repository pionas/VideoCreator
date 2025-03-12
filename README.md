# e-Konkursy Social Media

## Opis projektu

**e-Konkursy Social Media** to aplikacja Java oparta na Maven, która automatycznie generuje krótkie filmy promocyjne (rolki na Instagram) na podstawie danych zebranych z serwisu [e-konkursy.info](https://www.e-konkursy.info). Projekt wykorzystuje OpenCV oraz JavaCV do przetwarzania obrazów i tworzenia wideo, a także Jackson do obsługi JSON.

Aplikacja generuje filmy prezentujące najnowsze konkursy, kończące się dzisiaj, najpopularniejsze w danym tygodniu lub miesiącu oraz dedykowane wydarzeniom tematycznym (np. Dzień Kobiet). Gotowe filmy są automatycznie przesyłane na serwer.

## Funkcjonalności

- Pobieranie danych o konkursach z serwisu e-konkursy.info poprzez API
- Pobieranie obrazów związanych z konkursami
- Generowanie filmów o różnych tematach, np.:
  - **lastAdded** – ostatnio dodane konkursy
  - **topWeekly** – najpopularniejsze konkursy tygodnia
  - **topMonthly** – najpopularniejsze konkursy miesiąca
  - **endingToday** – konkursy kończące się dzisiaj
  - **womensDay** – konkursy związane z Dniem Kobiet
- Tworzenie dynamicznych scen z efektami wizualnymi
- Automatyczne czyszczenie plików tymczasowych po zakończeniu działania

## Wymagania

- **Java 21**
- **Maven**
- **FFmpeg** (do obsługi wideo)
- **OpenCV & JavaCV** (do przetwarzania obrazów i tworzenia wideo)

## Instalacja i uruchomienie

### 1. Klonowanie repozytorium

```sh
 git clone https://github.com/twoje-repozytorium/eKonkursySocialMedia.git
 cd eKonkursySocialMedia
```

### 2. Budowanie projektu

```sh
mvn clean package
```

### 3. Uruchomienie aplikacji

```sh
java -jar target/eKonkursySocialMedia-1.0-SNAPSHOT-shaded.jar lastAdded
```

lub dla innych typów filmów:

```sh
java -jar target/eKonkursySocialMedia-1.0-SNAPSHOT-shaded.jar topMonthly
```

## GitHub Actions Workflow

Projekt zawiera skonfigurowany workflow GitHub Actions, który pozwala na ręczne uruchamianie generowania filmów poprzez **workflow_dispatch**.

### Uruchamianie workflow

Można ręcznie wywołać workflow na GitHubie i określić typ filmu jako jeden z dostępnych:

- **lastAdded**
- **topWeekly**
- **topMonthly**
- **endingToday**

### Konfiguracja workflow

Workflow znajduje się w `.github/workflows` i zawiera:

```yaml
name: Generate Video and Commit

on:
  workflow_dispatch:
    inputs:
      video_type:
        description: 'Video type (lastAdded, topWeekly, topMonthly, endingToday)'
        required: true
        default: 'lastAdded'
```

Workflow ten umożliwia automatyczne uruchomienie generowania filmów bez potrzeby ręcznego uruchamiania aplikacji lokalnie.

## Struktura projektu

```
├── src/main/java/pl/excellentapp/ekonkursy/
│   ├── article/                 # Pobieranie danych o konkursach
│   ├── config/                  # Konfiguracja aplikacji
│   ├── core/                    # Obsługa pobierania plików, czyszczenie katalogów, uploader wideo
│   ├── image/                   # Przetwarzanie obrazów
│   ├── projects/                # Różne typy filmów
│   ├── scene/                   # Tworzenie scen i elementów wideo
│   ├── utils/                   # Klasy pomocnicze
│   ├── VideoCreator.java        # Główna klasa aplikacji
└── pom.xml                      # Konfiguracja projektu Maven
```

## Autor
Projekt stworzony przez **ExcellentApp**.

