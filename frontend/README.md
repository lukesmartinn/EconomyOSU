# EconomyOSU Frontend

React aplikace pro správu partnerů s integrací do registru ARES.

## Instalace

```bash
cd frontend
npm install
```

## Spuštění

```bash
npm start
```

Aplikace se otevře na [http://localhost:3000](http://localhost:3000)

## Funkce

- ✅ **Správa partnerů** - Vytváření, čtení, aktualizace, mazání
- ✅ **Vyhledávání** - Hledání podle názvu firmy nebo IČO
- ✅ **Integrace s ARES** - Automatické načítání údajů z ARES registru
- ✅ **Responzivní design** - Funguje na mobilu i desktopu
- ✅ **Moderní UI** - Intuitivní a přívětivé rozhraní

## Struktura projektu

```
frontend/
├── public/
│   └── index.html          # HTML šablona
├── src/
│   ├── components/         # React komponenty
│   │   ├── PartnerList.js  # Seznam partnerů
│   │   ├── PartnerForm.js  # Formulář pro vytvoření/úpravu
│   │   └── PartnerDetail.js # Detail partnera
│   ├── services/
│   │   └── api.js          # API klient
│   ├── App.js              # Hlavní komponent
│   ├── App.css
│   ├── index.js            # Entry point
│   └── index.css           # Globální styly
├── package.json
└── .env                    # Konfigurace (API URL)
```

## API Endpoints

Aplikace komunikuje s těmito endpoints:

- `GET /api/partners` - Vrátí všechny partnery
- `POST /api/partners` - Vytvoří nového partnera
- `POST /api/partners/ares-verify` - Vytvoří partnera s ověřením v ARES
- `GET /api/partners/{id}` - Vrátí konkrétního partnera
- `GET /api/partners/ico/{ico}` - Vyhledá partnera podle IČO
- `GET /api/partners/search?name={name}` - Vyhledá partnery podle názvu
- `PUT /api/partners/{id}` - Aktualizuje partnera
- `DELETE /api/partners/{id}` - Smaže partnera

## Konfigurace

Upravte soubor `.env`:

```
REACT_APP_API_URL=http://localhost:8080/api
```

## Postavení produkčního balíčku

```bash
npm run build
```

Výstup bude v `build/` adresáři.
