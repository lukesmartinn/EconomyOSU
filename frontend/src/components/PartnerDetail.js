import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Grid,
  Chip,
  Divider,
  CircularProgress,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Avatar,
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Business as BusinessIcon,
  Email as EmailIcon,
  Phone as PhoneIcon,
  LocationOn as LocationOnIcon,
  Description as DescriptionIcon,
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { toast } from 'react-toastify';
import { partnerAPI } from '../services/api';

function PartnerDetail() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [partner, setPartner] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  useEffect(() => {
    loadPartner();
  }, [id]);

  const loadPartner = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await partnerAPI.getPartnerById(id);
      setPartner(data);
    } catch (err) {
      setError(err.message);
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    try {
      await partnerAPI.deletePartner(id);
      toast.success('Partner byl úspěšně smazán');
      navigate('/');
    } catch (err) {
      toast.error(err.message);
      setError(err.message);
    }
    setDeleteDialogOpen(false);
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 400 }}>
        <CircularProgress size={60} />
      </Box>
    );
  }

  if (error || !partner) {
    return (
      <Box>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/')}
          sx={{ mb: 3 }}
        >
          Zpět na seznam
        </Button>
        <Alert severity="error">{error || 'Partner nenalezen'}</Alert>
      </Box>
    );
  }

  const InfoItem = ({ icon, label, value }) => (
    <Box sx={{ display: 'flex', alignItems: 'flex-start', mb: 2 }}>
      <Box
        sx={{
          mr: 2,
          mt: 0.5,
          color: 'primary.main',
          display: 'flex',
          alignItems: 'center',
        }}
      >
        {icon}
      </Box>
      <Box sx={{ flex: 1 }}>
        <Typography variant="caption" color="text.secondary" display="block">
          {label}
        </Typography>
        <Typography variant="body1" fontWeight={500}>
          {value || '-'}
        </Typography>
      </Box>
    </Box>
  );

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
    >
      <Box>
        {/* Header Actions */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
          <Button
            startIcon={<ArrowBackIcon />}
            onClick={() => navigate('/')}
            variant="outlined"
          >
            Zpět na seznam
          </Button>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button
              variant="contained"
              startIcon={<EditIcon />}
              onClick={() => navigate(`/partner/edit/${id}`)}
            >
              Upravit
            </Button>
            <Button
              variant="outlined"
              color="error"
              startIcon={<DeleteIcon />}
              onClick={() => setDeleteDialogOpen(true)}
            >
              Smazat
            </Button>
          </Box>
        </Box>

        <Grid container spacing={3}>
          {/* Header Card */}
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 3 }}>
                  <Avatar
                    sx={{
                      width: 80,
                      height: 80,
                      bgcolor: 'primary.main',
                      fontSize: '2rem',
                    }}
                  >
                    {partner.companyName?.charAt(0).toUpperCase()}
                  </Avatar>
                  <Box sx={{ flex: 1 }}>
                    <Typography variant="h4" fontWeight={600} gutterBottom>
                      {partner.companyName}
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                      <Chip
                        label={`IČO: ${partner.ico}`}
                        color="primary"
                        variant="outlined"
                      />
                      {partner.dic && (
                        <Chip
                          label={`DIČ: ${partner.dic}`}
                          color="secondary"
                          variant="outlined"
                        />
                      )}
                    </Box>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          {/* Contact Information */}
          <Grid item xs={12} md={6}>
            <Card sx={{ height: '100%' }}>
              <CardContent>
                <Typography variant="h6" fontWeight={600} gutterBottom>
                  Kontaktní údaje
                </Typography>
                <Divider sx={{ mb: 3 }} />

                <InfoItem
                  icon={<EmailIcon />}
                  label="Email"
                  value={partner.email}
                />
                <InfoItem
                  icon={<PhoneIcon />}
                  label="Telefon"
                  value={partner.phone}
                />
                <InfoItem
                  icon={<LocationOnIcon />}
                  label="Sídlo"
                  value={partner.address}
                />
              </CardContent>
            </Card>
          </Grid>

          {/* Company Information */}
          <Grid item xs={12} md={6}>
            <Card sx={{ height: '100%' }}>
              <CardContent>
                <Typography variant="h6" fontWeight={600} gutterBottom>
                  Firemní údaje
                </Typography>
                <Divider sx={{ mb: 3 }} />

                <InfoItem
                  icon={<BusinessIcon />}
                  label="Název firmy"
                  value={partner.companyName}
                />
                <InfoItem
                  icon={<DescriptionIcon />}
                  label="IČO"
                  value={partner.ico}
                />
                <InfoItem
                  icon={<DescriptionIcon />}
                  label="DIČ"
                  value={partner.dic}
                />
              </CardContent>
            </Card>
          </Grid>

          {/* Additional Info */}
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Typography variant="h6" fontWeight={600} gutterBottom>
                  Další informace
                </Typography>
                <Divider sx={{ mb: 3 }} />

                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6} md={3}>
                    <Box sx={{ textAlign: 'center', p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
                      <Typography variant="h4" color="primary.main" fontWeight={600}>
                        {partner.email ? '✓' : '✗'}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Email zadán
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={12} sm={6} md={3}>
                    <Box sx={{ textAlign: 'center', p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
                      <Typography variant="h4" color="primary.main" fontWeight={600}>
                        {partner.phone ? '✓' : '✗'}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Telefon zadán
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={12} sm={6} md={3}>
                    <Box sx={{ textAlign: 'center', p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
                      <Typography variant="h4" color="primary.main" fontWeight={600}>
                        {partner.dic ? '✓' : '✗'}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Plátce DPH
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={12} sm={6} md={3}>
                    <Box sx={{ textAlign: 'center', p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
                      <Typography variant="h4" color="primary.main" fontWeight={600}>
                        {partner.address ? '✓' : '✗'}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Adresa zadána
                      </Typography>
                    </Box>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Delete Confirmation Dialog */}
        <Dialog
          open={deleteDialogOpen}
          onClose={() => setDeleteDialogOpen(false)}
        >
          <DialogTitle>Smazat partnera?</DialogTitle>
          <DialogContent>
            <DialogContentText>
              Opravdu chcete smazat partnera "{partner.companyName}"? Tuto akci nelze vrátit zpět.
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDeleteDialogOpen(false)}>
              Zrušit
            </Button>
            <Button onClick={handleDelete} color="error" variant="contained">
              Smazat
            </Button>
          </DialogActions>
        </Dialog>
      </Box>
    </motion.div>
  );
}

export default PartnerDetail;
