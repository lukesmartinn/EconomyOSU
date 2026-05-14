import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// -------------------------------------------------------------------------
// Helpers
// -------------------------------------------------------------------------

/**
 * Vytáhne čitelnou chybovou zprávu z Axios error objektu.
 * Backend nyní vrací { message: "..." } v těle, takže ji zobrazíme přímo.
 */
function extractErrorMessage(error, fallback) {
  return error.response?.data?.message || fallback;
}

// -------------------------------------------------------------------------
// Partner API
// -------------------------------------------------------------------------
export const partnerAPI = {

  /** Vrátí všechny partnery z lokální DB. */
  getAllPartners: async () => {
    try {
      const response = await api.get('/partners');
      return response.data;
    } catch (error) {
      throw new Error(extractErrorMessage(error, 'Chyba při načítání partnerů'));
    }
  },

  /** Vrátí partnera podle ID. */
  getPartnerById: async (id) => {
    try {
      const response = await api.get(`/partners/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(extractErrorMessage(error, 'Chyba při načítání partnera'));
    }
  },

  /** Vrátí partnera podle IČO z lokální DB. */
  getPartnerByIco: async (ico) => {
    try {
      const response = await api.get(`/partners/ico/${ico}`);
      return response.data;
    } catch (error) {
      throw new Error(extractErrorMessage(error, 'Partner s tímto IČO nebyl nalezen'));
    }
  },

  /** Vyhledá partnery podle názvu (fulltextově). */
  searchPartners: async (name) => {
    try {
      const response = await api.get('/partners/search', { params: { name } });
      return response.data;
    } catch (error) {
      throw new Error(extractErrorMessage(error, 'Chyba při vyhledávání'));
    }
  },

  /**
   * Vyhledá subjekt v ARES podle IČO.
   * Neukládá partnera – pouze vrátí data pro předvyplnění formuláře.
   *
   * Endpoint: GET /api/partners/ares?ico={ico}
   *
   * Vrácená struktura:
   * {
   *   companyName: string,
   *   ico: string,
   *   dic: string,        // "" pokud firma není plátcem DPH
   *   address: string,    // "" pokud adresa není v ARES
   *   legalForm: string,
   *   registrationDate: string,
   * }
   */
  lookupPartnerWithAres: async (ico) => {
    try {
      const response = await api.get('/partners/ares', { params: { ico } });
      return response.data;
    } catch (error) {
      throw new Error(
        extractErrorMessage(error, 'Chyba při ověřování v ARES. Zkontrolujte IČO.')
      );
    }
  },

  /**
   * Vytvoří partnera s ověřením v ARES (načte data z ARES + uloží do DB v jednom volání).
   * Vhodné pouze pokud nechceme zobrazovat formulář – PartnerForm toto NEPOUŽÍVÁ.
   */
  createPartnerWithAres: async (ico, email, phone) => {
    try {
      const response = await api.post('/partners/ares-verify', null, {
        params: {
          ico,
          email: email || undefined,
          phone: phone || undefined,
        },
      });
      return response.data;
    } catch (error) {
      throw new Error(
        extractErrorMessage(error, 'Chyba při ověřování v ARES. Zkontrolujte IČO.')
      );
    }
  },

  /** Vytvoří partnera s manuálně zadanými údaji. */
  createPartner: async (partner) => {
    try {
      const response = await api.post('/partners', partner);
      return response.data;
    } catch (error) {
      throw new Error(extractErrorMessage(error, 'Chyba při vytváření partnera'));
    }
  },

  /** Aktualizuje existujícího partnera. */
  updatePartner: async (id, partner) => {
    try {
      const response = await api.put(`/partners/${id}`, partner);
      return response.data;
    } catch (error) {
      throw new Error(extractErrorMessage(error, 'Chyba při aktualizaci partnera'));
    }
  },

  /** Smaže partnera. */
  deletePartner: async (id) => {
    try {
      await api.delete(`/partners/${id}`);
    } catch (error) {
      throw new Error(extractErrorMessage(error, 'Chyba při mazání partnera'));
    }
  },
};

export default api;
