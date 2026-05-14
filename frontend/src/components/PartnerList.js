import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  CardActions,
  Typography,
  Button,
  TextField,
  InputAdornment,
  Grid,
  Chip,
  IconButton,
  Skeleton,
  Fade,
  Alert,
  Fab,
} from '@mui/material';
import {
  Search as SearchIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Visibility as VisibilityIcon,
  Add as AddIcon,
  Business as BusinessIcon,
  Email as EmailIcon,
  Phone as PhoneIcon,
  LocationOn as LocationOnIcon,
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { toast } from 'react-toastify';
import { partnerAPI } from '../services/api';

function PartnerList() {
  const navigate = useNavigate();
  const [partners, setPartners] = useState([]);
  const [filteredPartners, setFilteredPartners] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [error, setError] = useState(null);

  useEffect(() => {
    loadPartners();
  }, []);

  useEffect(() => {
    if (searchTerm) {
      const filtered = partners.filter(
        (partner) =>
          partner.companyName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
          partner.ico?.includes(searchTerm) ||
          partner.email?.toLowerCase().includes(searchTerm.toLowerCase())
      );
      setFilteredPartners(filtered);
    } else {
      setFilteredPartners(partners);
    }
  }, [searchTerm, partners]);

  const loadPartners = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await partnerAPI.getAllPartners();
      setPartners(data);
      setFilteredPartners(data);
      toast.success(`Načteno ${data.length} partnerů`);
    } catch (err) {
      setError(err.message);
      toast.error('Chyba při načítání partnerů');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id, companyName) => {
    if (window.confirm(`Opravdu chcete smazat partnera "${companyName}"?`)) {
      try {
        await partnerAPI.deletePartner(id);
        setPartners(partners.filter((p) => p.id !== id));
        toast.success('Partner byl úspěšně smazán');
      } catch (err) {
        toast.error('Chyba při mazání partnera');
      }
    }
  };

  const PartnerCard = ({ partner, index }) => (
    <Grid item xs={12} sm={6} md={4} key={partner.id}>
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3, delay: index * 0.05 }}
      >
        <Card
          sx={{
            height: '100%',
            display: 'flex',
            flexDirection: 'column',
            transition: 'transform 0.2s, box-shadow 0.2s',
            '&:hover': {
              transform: 'translateY(-4px)',
              boxShadow: 6,
            },
          }}
        >
          <CardContent sx={{ flexGrow: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <BusinessIcon sx={{ fontSize: 40, color: 'primary.main', mr: 1.5 }} />
              <Box sx={{ flexGrow: 1 }}>
                <Typography variant="h6" component="div" gutterBottom noWrap>
                  {partner.companyName}
                </Typography>
                <Chip
                  label={`IČO: ${partner.ico}`}
                  size="small"
                  color="primary"
                  variant="outlined"
                />
              </Box>
            </Box>

            {partner.dic && (
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <Typography variant="body2" color="text.secondary">
                  <strong>DIČ:</strong> {partner.dic}
                </Typography>
              </Box>
            )}

            {partner.address && (
              <Box sx={{ display: 'flex', alignItems: 'flex-start', mb: 1 }}>
                <LocationOnIcon sx={{ fontSize: 18, mr: 0.5, mt: 0.2, color: 'text.secondary' }} />
                <Typography variant="body2" color="text.secondary" sx={{ flex: 1 }}>
                  {partner.address}
                </Typography>
              </Box>
            )}

            {partner.email && (
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <EmailIcon sx={{ fontSize: 18, mr: 0.5, color: 'text.secondary' }} />
                <Typography variant="body2" color="text.secondary" noWrap>
                  {partner.email}
                </Typography>
              </Box>
            )}

            {partner.phone && (
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <PhoneIcon sx={{ fontSize: 18, mr: 0.5, color: 'text.secondary' }} />
                <Typography variant="body2" color="text.secondary">
                  {partner.phone}
                </Typography>
              </Box>
            )}
          </CardContent>

          <CardActions sx={{ justifyContent: 'space-between', px: 2, pb: 2 }}>
            <Button
              size="small"
              startIcon={<VisibilityIcon />}
              onClick={() => navigate(`/partner/${partner.id}`)}
            >
              Detail
            </Button>
            <Box>
              <IconButton
                size="small"
                color="primary"
                onClick={() => navigate(`/partner/edit/${partner.id}`)}
              >
                <EditIcon fontSize="small" />
              </IconButton>
              <IconButton
                size="small"
                color="error"
                onClick={() => handleDelete(partner.id, partner.companyName)}
              >
                <DeleteIcon fontSize="small" />
              </IconButton>
            </Box>
          </CardActions>
        </Card>
      </motion.div>
    </Grid>
  );

  if (loading) {
    return (
      <Box>
        <Typography variant="h4" gutterBottom fontWeight={600}>
          Partneři
        </Typography>
        <Grid container spacing={3} sx={{ mt: 2 }}>
          {[1, 2, 3, 4, 5, 6].map((i) => (
            <Grid item xs={12} sm={6} md={4} key={i}>
              <Card>
                <CardContent>
                  <Skeleton variant="text" width="60%" height={32} />
                  <Skeleton variant="text" width="40%" />
                  <Skeleton variant="rectangular" height={80} sx={{ mt: 2 }} />
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight={600}>
          Partneři ({filteredPartners.length})
        </Typography>
      </Box>

      <TextField
        fullWidth
        variant="outlined"
        placeholder="Vyhledat partnera podle názvu, IČO nebo emailu..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        sx={{ mb: 3 }}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon />
            </InputAdornment>
          ),
        }}
      />

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {filteredPartners.length === 0 ? (
        <Fade in>
          <Card sx={{ py: 8, textAlign: 'center' }}>
            <BusinessIcon sx={{ fontSize: 80, color: 'text.disabled', mb: 2 }} />
            <Typography variant="h6" color="text.secondary" gutterBottom>
              {searchTerm ? 'Žádní partneři nenalezeni' : 'Zatím nemáte žádné partnery'}
            </Typography>
            {!searchTerm && (
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => navigate('/partner/new')}
                sx={{ mt: 2 }}
              >
                Přidat prvního partnera
              </Button>
            )}
          </Card>
        </Fade>
      ) : (
        <Grid container spacing={3}>
          {filteredPartners.map((partner, index) => (
            <PartnerCard key={partner.id} partner={partner} index={index} />
          ))}
        </Grid>
      )}

      <Fab
        color="primary"
        aria-label="add"
        sx={{
          position: 'fixed',
          bottom: 32,
          right: 32,
        }}
        onClick={() => navigate('/partner/new')}
      >
        <AddIcon />
      </Fab>
    </Box>
  );
}

export default PartnerList;
