import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Grid,
  CircularProgress,
  Switch,
  FormControlLabel,
  Divider,
  Chip,
  Alert,
  Paper,
  InputAdornment,
} from '@mui/material';
import {
  Save as SaveIcon,
  Cancel as CancelIcon,
  Search as SearchIcon,
  CheckCircle as CheckCircleIcon,
  Business as BusinessIcon,
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { toast } from 'react-toastify';
import { partnerAPI } from '../services/api';

function PartnerForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = !!id;

  const [formData, setFormData] = useState({
    companyName: '',
    ico: '',
    dic: '',
    address: '',
    email: '',
    phone: '',
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [useAres, setUseAres] = useState(true);
  const [aresLoading, setAresLoading] = useState(false);
  const [aresPopulated, setAresPopulated] = useState(false);

  useEffect(() => {
    if (isEditMode) {
      loadPartner();
    }
  }, [id, isEditMode]);

  const loadPartner = async () => {
    try {
      setLoading(true);
      const partner = await partnerAPI.getPartnerById(id);
      setFormData(normalizePartnerData(partner));
    } catch (err) {
      toast.error(err.message);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const normalizePartnerData = (data) => ({
    companyName: data.companyName ?? '',
    ico: data.ico ?? '',
    dic: data.dic ?? '',
    address: data.address ?? '',
    email: data.email ?? '',
    phone: data.phone ?? '',
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (aresPopulated && ['companyName', 'dic', 'address'].includes(name)) {
      setAresPopulated(false);
    }
  };

  const handleAresLookup = async () => {
    const trimmedIco = formData.ico.trim();
    if (!trimmedIco) {
      toast.warning('Prosím, zadejte IČO');
      return;
    }

    const digitsOnly = trimmedIco.replace(/\D/g, '');
    if (digitsOnly.length !== 8) {
      toast.error('IČO musí mít přesně 8 číslic');
      return;
    }

    try {
      setAresLoading(true);
      setError(null);
      setAresPopulated(false);

      const aresData = await partnerAPI.lookupPartnerWithAres(digitsOnly);

      setFormData((prev) => ({
        ...prev,
        companyName: aresData.companyName || prev.companyName,
        dic: aresData.dic || prev.dic,
        address: aresData.address || prev.address,
      }));

      setAresPopulated(true);
      toast.success('Údaje z ARES byly úspěšně načteny');
    } catch (err) {
      toast.error(err.message);
      setError(err.message);
    } finally {
      setAresLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.companyName.trim() || !formData.ico.trim()) {
      toast.error('Název firmy a IČO jsou povinné');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      if (isEditMode) {
        await partnerAPI.updatePartner(id, formData);
        toast.success('Partner byl úspěšně aktualizován');
      } else {
        await partnerAPI.createPartner(formData);
        toast.success('Partner byl úspěšně vytvořen');
      }

      navigate('/');
    } catch (err) {
      toast.error(err.message);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  if (loading && isEditMode) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 400 }}>
        <CircularProgress size={60} />
      </Box>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
    >
      <Box>
        <Typography variant="h4" fontWeight={600} gutterBottom>
          {isEditMode ? 'Úprava partnera' : 'Nový partner'}
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
          {isEditMode ? 'Upravte údaje o partnerovi' : 'Přidejte nového obchodního partnera'}
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError(null)}>
            {error}
          </Alert>
        )}

        <Grid container spacing={3}>
          {/* ARES sekce */}
          {!isEditMode && (
            <Grid item xs={12}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                    <Typography variant="h6" fontWeight={600}>
                      Vyhledání v registru ARES
                    </Typography>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={useAres}
                          onChange={(e) => {
                            setUseAres(e.target.checked);
                            setAresPopulated(false);
                          }}
                        />
                      }
                      label="Použít ARES"
                    />
                  </Box>

                  {useAres && (
                    <>
                      <Alert severity="info" sx={{ mb: 2 }}>
                        Zadejte IČO a klikněte na tlačítko pro automatické načtení údajů z registru ARES
                      </Alert>

                      <Box sx={{ display: 'flex', gap: 2 }}>
                        <TextField
                          fullWidth
                          name="ico"
                          label="IČO"
                          value={formData.ico}
                          onChange={handleInputChange}
                          placeholder="např. 27082440"
                          inputProps={{ maxLength: 8, inputMode: 'numeric' }}
                          InputProps={{
                            startAdornment: (
                              <InputAdornment position="start">
                                <BusinessIcon />
                              </InputAdornment>
                            ),
                          }}
                        />
                        <Button
                          variant="contained"
                          size="large"
                          startIcon={aresLoading ? <CircularProgress size={20} color="inherit" /> : <SearchIcon />}
                          onClick={handleAresLookup}
                          disabled={aresLoading || !formData.ico.trim()}
                          sx={{ minWidth: 200 }}
                        >
                          {aresLoading ? 'Hledám...' : 'Vyhledat v ARES'}
                        </Button>
                      </Box>

                      {aresPopulated && (
                        <Paper
                          sx={{
                            mt: 2,
                            p: 2,
                            backgroundColor: 'success.light',
                            color: 'success.contrastText',
                            display: 'flex',
                            alignItems: 'center',
                            gap: 1,
                          }}
                        >
                          <CheckCircleIcon />
                          <Typography>
                            Údaje byly úspěšně načteny z ARES. Zkontrolujte je a případně doplňte.
                          </Typography>
                        </Paper>
                      )}
                    </>
                  )}
                </CardContent>
              </Card>
            </Grid>
          )}

          {/* Hlavní formulář */}
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Typography variant="h6" fontWeight={600} gutterBottom>
                  Základní údaje
                </Typography>
                <Divider sx={{ mb: 3 }} />

                <form onSubmit={handleSubmit}>
                  <Grid container spacing={3}>
                    <Grid item xs={12} md={6}>
                      <TextField
                        fullWidth
                        required
                        name="companyName"
                        label={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            Název firmy
                            {aresPopulated && <Chip label="ARES" size="small" color="success" />}
                          </Box>
                        }
                        value={formData.companyName}
                        onChange={handleInputChange}
                        placeholder="Zadejte název firmy"
                      />
                    </Grid>

                    <Grid item xs={12} md={6}>
                      <TextField
                        fullWidth
                        required
                        name="ico"
                        label="IČO"
                        value={formData.ico}
                        onChange={handleInputChange}
                        placeholder="Zadejte IČO"
                        disabled={isEditMode}
                        inputProps={{ maxLength: 8, inputMode: 'numeric' }}
                      />
                    </Grid>

                    <Grid item xs={12} md={6}>
                      <TextField
                        fullWidth
                        name="dic"
                        label={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            DIČ
                            {aresPopulated && formData.dic && <Chip label="ARES" size="small" color="success" />}
                          </Box>
                        }
                        value={formData.dic}
                        onChange={handleInputChange}
                        placeholder="Zadejte DIČ (nepovinné)"
                      />
                    </Grid>

                    <Grid item xs={12} md={6}>
                      <TextField
                        fullWidth
                        name="address"
                        label={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            Sídlo
                            {aresPopulated && formData.address && <Chip label="ARES" size="small" color="success" />}
                          </Box>
                        }
                        value={formData.address}
                        onChange={handleInputChange}
                        placeholder="Zadejte adresu sídla"
                      />
                    </Grid>

                    <Grid item xs={12} md={6}>
                      <TextField
                        fullWidth
                        type="email"
                        name="email"
                        label="Email"
                        value={formData.email}
                        onChange={handleInputChange}
                        placeholder="partner@firma.cz"
                      />
                    </Grid>

                    <Grid item xs={12} md={6}>
                      <TextField
                        fullWidth
                        type="tel"
                        name="phone"
                        label="Telefon"
                        value={formData.phone}
                        onChange={handleInputChange}
                        placeholder="+420 123 456 789"
                      />
                    </Grid>

                    <Grid item xs={12}>
                      <Divider sx={{ my: 2 }} />
                      <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                        <Button
                          variant="outlined"
                          size="large"
                          startIcon={<CancelIcon />}
                          onClick={() => navigate('/')}
                          disabled={loading}
                        >
                          Zrušit
                        </Button>
                        <Button
                          type="submit"
                          variant="contained"
                          size="large"
                          startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <SaveIcon />}
                          disabled={loading}
                        >
                          {loading ? 'Ukládám...' : isEditMode ? 'Uložit změny' : 'Vytvořit partnera'}
                        </Button>
                      </Box>
                    </Grid>
                  </Grid>
                </form>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Box>
    </motion.div>
  );
}

export default PartnerForm;
