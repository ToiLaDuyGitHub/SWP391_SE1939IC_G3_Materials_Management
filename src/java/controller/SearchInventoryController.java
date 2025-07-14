/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.MaterialDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Material;

/**
 *
 * @author Admin
 */
@WebServlet(name="SearchInventoryController", urlPatterns={"/search-inventory"})
public class SearchInventoryController extends HttpServlet {
   
    private MaterialDAO materialDAO;

    @Override
    public void init() throws ServletException {
        materialDAO = new MaterialDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String keyword = request.getParameter("keyword") != null ? request.getParameter("keyword").trim() : "";
        
        List<Material> materialList;
        if (keyword.isEmpty()) {
            materialList = materialDAO.getAllMaterialsWithQuantities();
        } else {
            materialList = materialDAO.searchInventoryMaterials(keyword);
        }
        
        request.setAttribute("materialList", materialList);
        request.getRequestDispatcher("/inventory.jsp").forward(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
