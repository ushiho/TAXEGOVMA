/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.CompteBanquaire;
import bean.Employe;
import bean.Societe;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ushiho
 */
@Stateless
public class CompteBanquaireFacade extends AbstractFacade<CompteBanquaire> {

    @EJB
    EmployeFacade employeFacade;
    @EJB
    BanqueFacade banqueFacade;

    @PersistenceContext(unitName = "TaxeGOVMAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CompteBanquaireFacade() {
        super(CompteBanquaire.class);
    }

    public List<CompteBanquaire> findBySociete(Societe societe) {

        return em.createQuery("SELECT c FROM CompteBanquaire c WHERE c.societe.id = '" + societe.getIdFiscal() + "'").getResultList();
    }

    public CompteBanquaire findByDGI() {
        String req = "SELECT c FROM CompteBanquaire c WHERE c.societe IS NULL";
        return getUniqueResult(req);
    }

    public int save(CompteBanquaire compteBanquaire) {
        if (testCompte(compteBanquaire)) {
            return -1;
        }
        create(compteBanquaire);
        return 1;
    }

    private boolean testCompte(CompteBanquaire compteBanquaire) {
        return compteBanquaire == null;
    }

    public int crediter(CompteBanquaire compteBanquaire, Float montant) {
        if (compteBanquaire == null) {
            return -1;
        } else if (compteBanquaire.getSolde() < montant) {
            return -2;
        } else {
            compteBanquaire.setSolde(compteBanquaire.getSolde() - montant);
            edit(compteBanquaire);
            return 1;
        }
    }

    public int debiter(CompteBanquaire compteBanquaire, Float montant) {
        if (compteBanquaire == null) {
            return -1;
        } else {
            compteBanquaire.setSolde(compteBanquaire.getSolde() + montant);
            edit(compteBanquaire);
            return 1;
        }
    }

    public CompteBanquaire clone(CompteBanquaire compteBanquaire) {
        CompteBanquaire clone = new CompteBanquaire(compteBanquaire.getId(), compteBanquaire.getSolde(), compteBanquaire.getRib());
        clone.setBanque(compteBanquaire.getBanque());
        clone.setSociete(compteBanquaire.getSociete());
        return clone;
    }

    public boolean existeInList(List<CompteBanquaire> compteBanquaires, CompteBanquaire compteBanquaire) {
        for (CompteBanquaire item : compteBanquaires) {
            return (item.getRib().equals(compteBanquaire.getRib()));
        }
        return false;
    }

    public int associatToUser(CompteBanquaire compteBanquaire, Employe employe) {
        if (testCompte(compteBanquaire) || employeFacade.testUtilisateur(employe)) {
            return -1;
        }
        compteBanquaire.setSociete(employe.getSociete());
        return 1;
    }

    public int saveListComptes(List<CompteBanquaire> compteBanquaires) {
        if (testList(compteBanquaires)) {
            return -1;
        }
        compteBanquaires.forEach((compteBanquaire) -> {
            save(compteBanquaire);
        });
        return 1;
    }

    private boolean testList(List<CompteBanquaire> compteBanquaires) {
        return compteBanquaires == null || compteBanquaires.isEmpty() || compteBanquaires.get(0) == null;
    }
}
